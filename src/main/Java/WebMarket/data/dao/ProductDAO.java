package WebMarket.data.dao;

import java.util.List;

import framework.data.DataException;
import model.Product;
import model.Order;

public interface ProductDAO {

    Product getProductById(int product_key) throws DataException;

    List<Product> getProductsByOrder(Order order) throws DataException;

    List<Product> getAllProducts() throws DataException;

    void addProduct(Product product) throws DataException;

    void updateProduct(Product product) throws DataException;

    void deleteProduct(Product product) throws DataException;
}