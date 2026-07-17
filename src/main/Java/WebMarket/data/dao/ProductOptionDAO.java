package WebMarket.data.dao;

import java.util.List;

import framework.data.DataException;
import model.ProductOption;
import model.ProductOptionGroup;


public interface ProductOptionDAO {

    ProductOption getProductOptionById(int id) throws DataException;

    List<ProductOption> getAllProductOptions() throws DataException;

    void addProductOption(ProductOption option) throws DataException;

    void updateProductOption(ProductOption option) throws DataException;

    void deleteProductOption(ProductOption option) throws DataException;

    List<ProductOption> getProductOptionsByProductOptionGroup(ProductOptionGroup group) throws DataException;

    List<ProductOption> getProductOptionsByProduct(int productId) throws DataException;
}
