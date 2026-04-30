package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.ProductOptionGroup;

public interface ProductOptionGroupDAO {

    ProductOptionGroup getProductOptionGroupById(int id) throws DataException;

    ProductOptionGroup getProductOptionGroupByName(String name) throws DataException;

    List<ProductOptionGroup> getAllProductOptionGroups() throws DataException;

    void addProductOptionGroup(ProductOptionGroup productOptionGroup) throws DataException;
    void updateProductOptionGroup(ProductOptionGroup productOptionGroup) throws DataException;
    void deleteProductOptionGroup(ProductOptionGroup productOptionGroup) throws DataException;



}
