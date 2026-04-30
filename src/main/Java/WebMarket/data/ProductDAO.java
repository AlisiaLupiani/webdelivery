package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.Product;


public interface ProductDAO {

    Product getProductById(Product product) throws DataException;
    Product getproductByName(Product product) throws DataException;
    Product getProductByPrice(Product product) throws DataException;
    Product getProductByCategory(Product product) throws DataException;
    List<Product> getAllProducts() throws DataException;
    
    void addProduct(Product product) throws DataException;
    void updateProduct(Product product) throws DataException;
    void deleteProduct(Product product) throws DataException;




}
