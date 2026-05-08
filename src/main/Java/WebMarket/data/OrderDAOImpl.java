package WebMarket.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Order;
import model.Client;
import WebMarket.data.proxy.OrderProxy;

public class OrderDAOImpl extends DAO implements OrderDAO {

    private PreparedStatement sOrderById;

    public OrderDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sOrderById = getConnection().prepareStatement("SELECT * FROM orders WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Errore durante l'inizializzazione del DAO Order", ex);
        }
    }

    protected Order createOrder(ResultSet rs) throws SQLException {
        OrderProxy o = new OrderProxy(getDataLayer());
        o.setKey(rs.getInt("id"));
        o.setVersion(rs.getLong("version"));
        // I campi date/time e client verranno gestiti dal proxy o settati qui
        return o;
    }

    @Override
    public Order getOrderById(int order_key) throws DataException {
        try {
            sOrderById.setInt(1, order_key);
            try (ResultSet rs = sOrderById.executeQuery()) {
                if (rs.next()) return createOrder(rs);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero Order per ID", ex);
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws DataException {
        return new ArrayList<>(); // Implementa query per data
    }

    @Override
    public List<Order> getOrdersByClient(Client client) throws DataException {
        return new ArrayList<>(); // Implementa query per cliente
    }

    @Override
    public List<Order> getAllOrders() throws DataException {
        List<Order> res = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM orders")) {
            while (rs.next()) res.add(createOrder(rs));
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero di tutti gli ordini", ex);
        }
        return res;
    }

    @Override public void addOrder(Order order) throws DataException {}
    @Override public void updateOrder(Order order) throws DataException {}
    @Override public void deleteOrder(Order order) throws DataException {}
}