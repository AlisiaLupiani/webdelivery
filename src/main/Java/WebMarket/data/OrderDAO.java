package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.Order;

public interface OrderDAO {

    Order getOrderById(Order order) throws DataException;
    Order getOrderByClient(Order order) throws DataException;
    Order getOrderByDate(Order order) throws DataException;
    List<Order> getAllOrders() throws DataException;

    void addOrder(Order order) throws DataException;
    void updateOrder(Order order) throws DataException;
    void deleteOrder(Order order) throws DataException;


}
