package WebMarket.data;

import java.util.List;
import framework.data.DataException;
import model.ProductOption;

public interface ProductOptionDAO {

    ProductOption getProductOptionById(int product_option_key) throws DataException;

    List<ProductOption> getAllProductOptions() throws DataException;

    void addProductOption(ProductOption option) throws DataException;

    void updateProductOption(ProductOption option) throws DataException;

    void deleteProductOption(ProductOption option) throws DataException;
}