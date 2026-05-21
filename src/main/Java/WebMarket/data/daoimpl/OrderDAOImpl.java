package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private static final String TABLE = "ORDINE";

    public OrderDAOImpl(DataLayer d) {
        super(d);

    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sOrderById = getConnection().prepareStatement("SELECT FROM" + TABLE + "WHERE ID=?");
            sOrderByDate = getConnection().prepareStatement("SELECT FROM" + TABLE + "WHERE DATA_ORDINE =?");
            sOrderByClient = getConnection().prepareStatement("SELECT FROM" + TABLE + "WHERE UTENTE_ID =?");
            sAllOrders = getConnection().prepareStatement("SELECT FROM" + TABLE);
            sAddOrder = getConnection().prepareStatement(
                    "INSERT INTO" + TABLE
                            + "(DATA_ORDINE, ORARIO CONSEGNA, PREZZO_TOTALE, STATO, METODO_PAGAMENTO, INDIRIZZO_CONSEGNA, UTENTE_ID) VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            sUpdateOrder = getConnection().prepareStatement(
                    "UPDATE" + TABLE
                            + "SET DATA_ORDINE=?, ORARIO CONSEGNA=?, PREZZO_TOTALE=?, STATO=?, METODO_PAGAMENTO=?, INDIRIZZO_CONSEGNA=?, UTENTE_ID=? WHERE ID=? AND VERSION=?");
            sDeleteOrder = getConnection().prepareStatement("DELETE FROM" + TABLE + "WHERE ID=? AND VERSION=?");

        } catch (SQLException ex) {
            throw new DataException("Errore durante l'inizializzazione del DAO Order", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sOrderById != null)
                sOrderById.close();
            if (sOrderByDate != null)
                sOrderByDate.close();
            if (sOrderByClient != null)
                sOrderByClient.close();
            if (sAllOrders != null)
                sAllOrders.close();
            if (sAddOrder != null)
                sAddOrder.close();
            if (sUpdateOrder != null)
                sUpdateOrder.close();
            if (sDeleteOrder != null)
                sDeleteOrder.close();
            super.destroy();

        } catch (SQLException e) {
            throw new DataException("Errore durante la chiusura del DAO Order", e);
        }

    }

    protected Order createOrder(ResultSet rs) throws SQLException {
        OrderProxy order = new OrderProxy(getDataLayer());
        order.setKey(rs.getInt("ID"));
        order.setVersion(rs.getLong("VERSION"));
        order.setDate(rs.getDate("DATA_ORDINE").toLocalDate());
        order.setDeliveryTime(rs.getTime("ORARIO CONSEGNA").toLocalTime());
        order.setPrice(rs.getDouble("PREZZO_TOTALE"));
        order.setDeliveryAddress(rs.getString("INDIRIZZO_CONSEGNA"));

        String state = rs.getString("STATO");
        if (state != null){
            order.setOrderState(OrderState.valueOf(state));
        }
        
        String paymentMethod = rs.getString("METODO_PAGAMENTO");
        if (paymentMethod != null){
            order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        }
        order.setIdUtenteNascosto(rs.getInt("UTENTE_ID"));

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
                throw new DataException("Errore nel recupero dell'ordine", e);
            }

        }
        return order;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws DataException {
        List<Order> res = new ArrayList<>();
        Order order;
        if (getDataLayer().getCache().has(Order.class, date)) {
            order = getDataLayer().getCache().get(Order.class, date);
            res.add(order);
        } else {
            try {
                sOrderByDate.setDate(1, java.sql.Date.valueOf(date));
                try (ResultSet rs = sOrderByDate.executeQuery()) {
                    while (rs.next()) {
                        order = createOrder(rs);
                        getDataLayer().getCache().add(Order.class, order);
                        res.add(order);
                    }
                }

            } catch (SQLException e) {
                throw new DataException("Errore nel recupero degli ordini", e);
            }

        }
        return res;
    }

    @Override
    public List<Order> getOrdersByClient(Client client) throws DataException {
        List<Order> res = new ArrayList<>();
        Order order;
        if (getDataLayer().getCache().has(Order.class, client.getKey())) {
            order = getDataLayer().getCache().get(Order.class, client.getKey());
            res.add(order);
        } else {
            try {
                sOrderByClient.setInt(1, client.getKey());
                try (ResultSet rs = sOrderByClient.executeQuery()) {
                    while (rs.next()) {
                        order = createOrder(rs);
                        getDataLayer().getCache().add(Order.class, order);
                        res.add(order);
                    }
                }

            } catch (SQLException e) {
                throw new DataException("Errore nel recupero degli ordini", e);
            }
        }
        return res;
    }

    @Override
    public List<Order> getAllOrders() throws DataException {
        List<Order> res = new ArrayList<>();
        try (ResultSet rs = sAllOrders.executeQuery()) {
            while (rs.next()) {
                Order order;
                Integer id = rs.getInt("ID");
                if (getDataLayer().getCache().has(Order.class, id)) {
                    order = getDataLayer().getCache().get(Order.class, id);
                } else {
                    order = createOrder(rs);
                    getDataLayer().getCache().add(Order.class, order);
                }
                res.add(order);
            }

        } catch (SQLException e) {
            throw new DataException("Errore nel recupero di tutti gli ordini", e);
        }
        return res;
    }

    @Override
    public void addOrder(Order order) throws DataException {
        try {
            sAddOrder.setString(1, order.getDate().toString());
            sAddOrder.setString(2, order.getDeliveryTime().toString());
            sAddOrder.setDouble(3, order.getPrice());
            sAddOrder.setString(4, order.getOrderState().name());
            sAddOrder.setString(5, order.getPaymentMethod().name());
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
            sUpdateOrder.setString(1, order.getDate().toString());
            sUpdateOrder.setString(2, order.getDeliveryTime().toString());
            sUpdateOrder.setDouble(3, order.getPrice());
            sUpdateOrder.setString(4, order.getOrderState().name());
            sUpdateOrder.setString(5, order.getPaymentMethod().name());
            sUpdateOrder.setString(6, order.getDeliveryAddress());
            sUpdateOrder.setInt(7, order.getClient().getKey());

            long currentVersion = order.getVersion();
            long nextVersion = currentVersion + 1;
            sUpdateOrder.setLong(8, nextVersion);
            sUpdateOrder.setInt(9, order.getKey());
            sUpdateOrder.setLong(10, currentVersion);

            if (sUpdateOrder.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed: order modified by another process");
            } else {
                order.setVersion(nextVersion);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to update order", e);
        }

    }

    @Override
    public void deleteOrder(Order order) throws DataException {
        try {
            sDeleteOrder.setInt(1, order.getKey());
            if (sDeleteOrder.executeUpdate() > 0) {
                getDataLayer().getCache().delete(Order.class, order.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Unable to delete order", e);
        }
    }

}