package WebMarket.data.dao;

import java.util.List;
import java.util.Map;

import framework.data.DataException;
import model.Order;
import model.Product;

public interface ProductDAO {

    Product getProductById(int product_key) throws DataException;

    List<Product> getProductsByOrder(Order order) throws DataException;

    List<Product> getAllProducts() throws DataException;

    void addProduct(Product product) throws DataException;

    void updateProduct(Product product) throws DataException;

    void deleteProduct(Product product) throws DataException;

    List<Map<String, Object>> getOrderProductDetails(Order order) throws DataException;

    List<Map<String, String>> getIngredientsByProductId(int productId) throws DataException;

    void addIngredientToProduct(int productId, int ingredientId, String quantity) throws DataException;

    void removeIngredientFromProduct(int productId, int ingredientId) throws DataException;

    List<Map<String, Object>> getOptionsByProductId(int productId) throws DataException;

    void addOptionToProduct(int productId, int optionId) throws DataException;

    void removeOptionFromProduct(int productId, int optionId) throws DataException;
}