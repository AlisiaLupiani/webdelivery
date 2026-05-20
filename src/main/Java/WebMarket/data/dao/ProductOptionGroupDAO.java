package WebMarket.data.dao;

import java.util.List;

import framework.data.DataException;
import model.Product;
import model.ProductOptionGroup;

public interface ProductOptionGroupDAO {

    ProductOptionGroup getProductOptionGroupById(int group_key) throws DataException;

    List<ProductOptionGroup> getAllProductOptionGroups() throws DataException;

    List<ProductOptionGroup> getProductOptionGroupsByProduct(Product product) throws DataException;

    void addProductOptionGroup(ProductOptionGroup group) throws DataException;

    void updateProductOptionGroup(ProductOptionGroup group) throws DataException;

    void deleteProductOptionGroup(ProductOptionGroup group) throws DataException;
}