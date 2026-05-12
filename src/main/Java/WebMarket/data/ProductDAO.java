package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.Product;

public interface ProductDAO {

    Product getProductById(int product_key) throws DataException;

    List<Product> getProductsByOrder(int orderId) throws DataException;

    List<Product> getAllProducts() throws DataException;

    void addProduct(Product product) throws DataException;

    void updateProduct(Product product) throws DataException;

    void deleteProduct(Product product) throws DataException;
}