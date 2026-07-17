package WebMarket.data.dao;

import java.time.LocalDate;
import java.util.List;

import framework.data.DataException;
import model.CartItem;
import model.Client;
import model.Order;
import model.ProductOption;

public interface OrderDAO {

    Order getOrderById(int order_key) throws DataException;

    Order getOrderByIdAndClientId(int orderKey, int clientId) throws DataException;

    List<Order> getOrdersByDate(LocalDate date) throws DataException;

    List<Order> getOrdersByClient(Client client) throws DataException;

    List<Order> getAllOrders() throws DataException;

    void addOrder(Order order) throws DataException;

    void updateOrder(Order order) throws DataException;

    void deleteOrder(Order order) throws DataException;

    int addProductToOrder(int orderId, CartItem item) throws DataException;
 
    void addOptionToOrderProduct(int orderProductId, ProductOption option) throws DataException;

}
