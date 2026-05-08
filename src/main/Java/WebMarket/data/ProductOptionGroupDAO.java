package WebMarket.data;

import java.util.List;
import framework.data.DataException;
import model.ProductOptionGroup;
import model.Product;

public interface ProductOptionGroupDAO {

    ProductOptionGroup getProductOptionGroupById(int group_key) throws DataException;

    List<ProductOptionGroup> getAllProductOptionGroups() throws DataException;

    List<ProductOptionGroup> getProductOptionGroupsByProduct(Product product) throws DataException;

    void addProductOptionGroup(ProductOptionGroup group) throws DataException;

    void updateProductOptionGroup(ProductOptionGroup group) throws DataException;

    void deleteProductOptionGroup(ProductOptionGroup group) throws DataException;
}