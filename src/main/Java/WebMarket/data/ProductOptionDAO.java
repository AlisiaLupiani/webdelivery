package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.ProductOption;

public interface ProductOptionDAO {

    ProductOption getProductOptionById(ProductOption productOption) throws DataException;
    ProductOption getProductOptionByName(ProductOption productOption) throws DataException;
    ProductOption getProductOptionByProductOptionGroup(ProductOption productOption) throws DataException;
    List<ProductOption> getAllProductOptions(ProductOption productOption) throws DataException;

    void insertProductOption(ProductOption productOption) throws DataException;
    void updateProductOption(ProductOption productOption) throws DataException;
    void deleteProductOption(ProductOption productOption) throws DataException;

}
