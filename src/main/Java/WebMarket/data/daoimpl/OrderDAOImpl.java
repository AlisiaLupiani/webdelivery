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
import model.Client;
import model.Order;
import model.OrderState;
import model.PaymentMethod;

public class OrderDAOImpl extends DAO implements OrderDAO {

    private PreparedStatement sOrderById;
    private PreparedStatement sOrderByDate;
    private PreparedStatement sOrderByClient;
    private PreparedStatement sAllOrders;
    private PreparedStatement sAddOrder;
    private PreparedStatement sUpdateOrder;
    private PreparedStatement sDeleteOrder;
    private PreparedStatement sAddProductToOrder;
    private static final String TABLE = "ORDINE";

    public OrderDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sOrderById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
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
    "INSERT INTO ORDINE_PRODOTTO (ORDINE_ID, PRODOTTO_ID, QUANTITA) VALUES (?, ?, ?) " +
    "ON DUPLICATE KEY UPDATE QUANTITA = QUANTITA + VALUES(QUANTITA)"
);
        } catch (SQLException ex) {
            throw new DataException("Error initializing OrderDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sOrderById != null) sOrderById.close();
            if (sOrderByDate != null) sOrderByDate.close();
            if (sOrderByClient != null) sOrderByClient.close();
            if (sAllOrders != null) sAllOrders.close();
            if (sAddOrder != null) sAddOrder.close();
            if (sUpdateOrder != null) sUpdateOrder.close();
            if (sDeleteOrder != null) sDeleteOrder.close();
            if (sAddProductToOrder != null) sAddProductToOrder.close();
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
public void addProductToOrder(int orderId, int productId, int quantity) throws DataException {
    try {
        sAddProductToOrder.setInt(1, orderId);
        sAddProductToOrder.setInt(2, productId);
        sAddProductToOrder.setInt(3, quantity);
        sAddProductToOrder.executeUpdate();
    } catch (SQLException ex) {
        throw new DataException("Errore addProductToOrder", ex);
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
