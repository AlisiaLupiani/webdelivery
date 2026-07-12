package WebMarket.data.dao;

import java.time.LocalDate;
import java.util.List;

import framework.data.DataException;
import model.Client;
import model.Order;

public interface OrderDAO {

    Order getOrderById(int order_key) throws DataException;

    List<Order> getOrdersByDate(LocalDate date) throws DataException;

    List<Order> getOrdersByClient(Client client) throws DataException;

    List<Order> getAllOrders() throws DataException;

    void addOrder(Order order) throws DataException;

    void updateOrder(Order order) throws DataException;

    void deleteOrder(Order order) throws DataException;

    void addProductToOrder(int orderId, int productId, int quantity) throws DataException;
}