package WebMarket.data.dao;

import java.util.List;

import framework.data.DataException;
import model.ProductOption;
import model.ProductOptionGroup;


public interface ProductOptionDAO {

    ProductOption getProductOptionById(int id) throws DataException;

    ProductOption geProductOptionByProductOptionGroup(ProductOptionGroup group) throws DataException;

    List<ProductOption> getAllProductOptions() throws DataException;

    void addProductOption(ProductOption option) throws DataException;

    void updateProductOption(ProductOption option) throws DataException;

    void deleteProductOption(ProductOption option) throws DataException;
}