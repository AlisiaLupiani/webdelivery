package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.OrderDAO;
import WebMarket.data.proxy.OrderProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.CartItem;
import model.Client;
import model.Order;
import model.OrderState;
import model.PaymentMethod;
import model.Product;
import model.ProductOption;
import model.ProductOptionGroup;

public class OrderDAOImpl extends DAO implements OrderDAO {

    private PreparedStatement sOrderById;
    private PreparedStatement sOrderByIdAndClient;
    private PreparedStatement sOrderByDate;
    private PreparedStatement sOrderByClient;
    private PreparedStatement sAllOrders;
    private PreparedStatement sAddOrder;
    private PreparedStatement sUpdateOrder;
    private PreparedStatement sDeleteOrder;
    private PreparedStatement sAddProductToOrder;
    private PreparedStatement sAddOptionToOrderProduct;
    private static final String TABLE = "ORDINE";

    public OrderDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sOrderById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
            sOrderByIdAndClient = getConnection().prepareStatement(
                    "SELECT * FROM " + TABLE + " WHERE ID = ? AND UTENTE_ID = ?"
            );
            sOrderByDate = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE DATA_ORDINE = ?");
            sOrderByClient = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE UTENTE_ID = ? ORDER BY DATA_ORDINE DESC, ORARIO_CONSEGNA DESC");
            sAllOrders = getConnection().prepareStatement("SELECT * FROM " + TABLE + " ORDER BY DATA_ORDINE DESC, ORARIO_CONSEGNA DESC");

            sAddOrder = getConnection().prepareStatement(
                    "INSERT INTO " + TABLE + " (DATA_ORDINE, ORARIO_CONSEGNA, PREZZO_TOTALE, STATO, METODO_PAGAMENTO, INDIRIZZO_CONSEGNA, UTENTE_ID, VERSION) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            sUpdateOrder = getConnection().prepareStatement(
                    "UPDATE " + TABLE + " SET DATA_ORDINE = ?, ORARIO_CONSEGNA = ?, PREZZO_TOTALE = ?, STATO = ?, METODO_PAGAMENTO = ?, INDIRIZZO_CONSEGNA = ?, UTENTE_ID = ?, VERSION = ? WHERE ID = ? AND VERSION = ?");

            sDeleteOrder = getConnection().prepareStatement("DELETE FROM " + TABLE + " WHERE ID = ? AND VERSION = ?");
            sAddProductToOrder = getConnection().prepareStatement(
                    "INSERT INTO ORDINE_PRODOTTO " +
                    "(ORDINE_ID, PRODOTTO_ID, QUANTITA, NOME_PRODOTTO, DESCRIZIONE_PRODOTTO, " +
                    "PREZZO_UNITARIO, PROCEDURA_PRODOTTO, TEMPO_PREPARAZIONE) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            sAddOptionToOrderProduct = getConnection().prepareStatement(
                    "INSERT INTO ORDINE_PRODOTTO_OPZIONE " +
                    "(ORDINE_PRODOTTO_ID, OPZIONE_ID, NOME_OPZIONE, DESCRIZIONE_OPZIONE, " +
                    "PREZZO_OPZIONE, NOME_GRUPPO) VALUES (?, ?, ?, ?, ?, ?)"
            );
        } catch (SQLException ex) {
            throw new DataException("Error initializing OrderDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sOrderById != null) sOrderById.close();
            if (sOrderByIdAndClient != null) sOrderByIdAndClient.close();
            if (sOrderByDate != null) sOrderByDate.close();
            if (sOrderByClient != null) sOrderByClient.close();
            if (sAllOrders != null) sAllOrders.close();
            if (sAddOrder != null) sAddOrder.close();
            if (sUpdateOrder != null) sUpdateOrder.close();
            if (sDeleteOrder != null) sDeleteOrder.close();
            if (sAddProductToOrder != null) sAddProductToOrder.close();
            if (sAddOptionToOrderProduct != null) sAddOptionToOrderProduct.close();
            super.destroy();
        } catch (SQLException e) {
            throw new DataException("Error closing OrderDAO", e);
        }
    }

    protected Order createOrder(ResultSet rs) throws SQLException {
        OrderProxy order = new OrderProxy(getDataLayer());
        order.setKey(rs.getInt("ID"));
        order.setVersion(rs.getLong("VERSION"));
        order.setDate(rs.getDate("DATA_ORDINE").toLocalDate());
        Time deliveryTime = rs.getTime("ORARIO_CONSEGNA");
        if (deliveryTime != null) {
            order.setDeliveryTime(deliveryTime.toLocalTime());
        }
        order.setPrice(rs.getDouble("PREZZO_TOTALE"));
        order.setDeliveryAddress(rs.getString("INDIRIZZO_CONSEGNA"));
        order.setOrderState(parseOrderState(rs.getString("STATO")));
        order.setPaymentMethod(parsePaymentMethod(rs.getString("METODO_PAGAMENTO")));
        order.setIdUtente(rs.getInt("UTENTE_ID"));
        order.setClean();
        return order;
    }

    @Override
    public Order getOrderById(int order_key) throws DataException {
        Order order = null;
        if (getDataLayer().getCache().has(Order.class, order_key)) {
            order = getDataLayer().getCache().get(Order.class, order_key);
        } else {
            try {
                sOrderById.setInt(1, order_key);
                try (ResultSet rs = sOrderById.executeQuery()) {
                    if (rs.next()) {
                        order = createOrder(rs);
                        getDataLayer().getCache().add(Order.class, order);
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Unable to retrieve order by ID", e);
            }
        }
        return order;
    }

    @Override
    public Order getOrderByIdAndClientId(int orderKey, int clientId) throws DataException {
        try {
            sOrderByIdAndClient.setInt(1, orderKey);
            sOrderByIdAndClient.setInt(2, clientId);

            try (ResultSet rs = sOrderByIdAndClient.executeQuery()) {
                if (rs.next()) {
                    Order order = createOrder(rs);
                    getDataLayer().getCache().add(Order.class, order);
                    return order;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve order for client", ex);
        }

        return null;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws DataException {
        List<Order> result = new ArrayList<>();
        try {
            sOrderByDate.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = sOrderByDate.executeQuery()) {
                while (rs.next()) {
                    result.add(getOrCreateOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataException("Unable to retrieve orders by date", e);
        }
        return result;
    }

    @Override
    public List<Order> getOrdersByClient(Client client) throws DataException {
        List<Order> result = new ArrayList<>();
        try {
            sOrderByClient.setInt(1, client.getKey());
            try (ResultSet rs = sOrderByClient.executeQuery()) {
                while (rs.next()) {
                    result.add(getOrCreateOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataException("Unable to retrieve orders by client", e);
        }
        return result;
    }

    @Override
    public List<Order> getAllOrders() throws DataException {
        List<Order> result = new ArrayList<>();
        try (ResultSet rs = sAllOrders.executeQuery()) {
            while (rs.next()) {
                result.add(getOrCreateOrder(rs));
            }
        } catch (SQLException e) {
            throw new DataException("Unable to retrieve all orders", e);
        }
        return result;
    }

    @Override
    public void addOrder(Order order) throws DataException {
        try {
            sAddOrder.setDate(1, java.sql.Date.valueOf(order.getDate()));
            sAddOrder.setTime(2, order.getDeliveryTime() != null ? Time.valueOf(order.getDeliveryTime()) : null);
            sAddOrder.setDouble(3, order.getPrice());
            sAddOrder.setString(4, order.getOrderState().name());
            sAddOrder.setString(5, order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
            sAddOrder.setString(6, order.getDeliveryAddress());
            sAddOrder.setInt(7, order.getClient().getKey());

            long initialVersion = 1;
            sAddOrder.setLong(8, initialVersion);

            if (sAddOrder.executeUpdate() == 1) {
                try (ResultSet resultSet = sAddOrder.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        Integer newKey = resultSet.getInt(1);
                        order.setKey(newKey);
                        order.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(Order.class, order);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to add order", e);
        }
    }

    @Override
    public void updateOrder(Order order) throws DataException {
        try {
            sUpdateOrder.setDate(1, java.sql.Date.valueOf(order.getDate()));
            sUpdateOrder.setTime(2, order.getDeliveryTime() != null ? Time.valueOf(order.getDeliveryTime()) : null);
            sUpdateOrder.setDouble(3, order.getPrice());
            sUpdateOrder.setString(4, order.getOrderState().name());
            sUpdateOrder.setString(5, order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
            sUpdateOrder.setString(6, order.getDeliveryAddress());
            sUpdateOrder.setInt(7, order.getClient().getKey());

            long currentVersion = order.getVersion();
            long nextVersion = currentVersion + 1;
            sUpdateOrder.setLong(8, nextVersion);
            sUpdateOrder.setInt(9, order.getKey());
            sUpdateOrder.setLong(10, currentVersion);

            if (sUpdateOrder.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed: order modified by another process");
            }
            order.setVersion(nextVersion);
            getDataLayer().getCache().add(Order.class, order);
        } catch (SQLException e) {
            throw new DataException("Unable to update order", e);
        }
    }

    @Override
    public void deleteOrder(Order order) throws DataException {
        try {
            sDeleteOrder.setInt(1, order.getKey());
            sDeleteOrder.setLong(2, order.getVersion());
            if (sDeleteOrder.executeUpdate() > 0) {
                getDataLayer().getCache().delete(Order.class, order.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Unable to delete order", e);
        }
    }
    @Override
    public int addProductToOrder(int orderId, CartItem item) throws DataException {
        Product product = item.getProdotto();

        if (product == null) {
            throw new DataException("Prodotto mancante nella riga del carrello");
        }

        try {
            sAddProductToOrder.setInt(1, orderId);
            sAddProductToOrder.setInt(2, product.getKey());
            sAddProductToOrder.setInt(3, item.getQuantita());
            sAddProductToOrder.setString(4, product.getName());
            sAddProductToOrder.setString(5, product.getDescription());
            sAddProductToOrder.setDouble(6, item.getPrezzoUnitario());
            sAddProductToOrder.setString(7, product.getProcedure());
            sAddProductToOrder.setInt(8, product.getPreparationTime());

            if (sAddProductToOrder.executeUpdate() != 1) {
                throw new DataException("Impossibile salvare la riga dell'ordine");
            }

            try (ResultSet rs = sAddProductToOrder.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new DataException("Chiave della riga ordine non disponibile");
        } catch (SQLException ex) {
            throw new DataException("Errore addProductToOrder", ex);
        }
    }

    @Override
    public void addOptionToOrderProduct(int orderProductId, ProductOption option) throws DataException {
        ProductOptionGroup group = option.getProductOptionGroup();

        try {
            sAddOptionToOrderProduct.setInt(1, orderProductId);
            sAddOptionToOrderProduct.setInt(2, option.getKey());
            sAddOptionToOrderProduct.setString(3, option.getName());
            sAddOptionToOrderProduct.setString(4, option.getDescription());
            sAddOptionToOrderProduct.setDouble(5, option.getAddictionalPrice());
            sAddOptionToOrderProduct.setString(6, group != null ? group.getName() : "");

            if (sAddOptionToOrderProduct.executeUpdate() != 1) {
                throw new DataException("Impossibile salvare la caratteristica della riga ordine");
            }
        } catch (SQLException ex) {
            throw new DataException("Errore addOptionToOrderProduct", ex);
        }
    }

    private Order getOrCreateOrder(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("ID");
        if (getDataLayer().getCache().has(Order.class, id)) {
            return getDataLayer().getCache().get(Order.class, id);
        }
        Order order = createOrder(rs);
        getDataLayer().getCache().add(Order.class, order);
        return order;
    }

    private OrderState parseOrderState(String value) {
        return value != null ? OrderState.valueOf(value) : null;
    }

    private PaymentMethod parsePaymentMethod(String value) {
        if (value == null) {
            return null;
        }
        if ("CONTANTI".equals(value)) {
            return PaymentMethod.CASH;
        }
        if ("CARTA_CREDITO".equals(value)) {
            return PaymentMethod.VISA;
        }
        return PaymentMethod.valueOf(value);
    }
}
