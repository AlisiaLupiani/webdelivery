package WebMarket.data;

import java.util.List;
import java.time.LocalDate;
import framework.data.DataException;
import model.Order;
import model.Client;

public interface OrderDAO {

    Order getOrderById(int order_key) throws DataException;

    List<Order> getOrdersByDate(LocalDate date) throws DataException;

    List<Order> getOrdersByClient(Client client) throws DataException;

    List<Order> getAllOrders() throws DataException;

    void addOrder(Order order) throws DataException;

    void updateOrder(Order order) throws DataException;

    void deleteOrder(Order order) throws DataException;
}