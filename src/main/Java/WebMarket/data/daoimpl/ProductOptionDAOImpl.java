package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.ProductOptionDAO;
import WebMarket.data.proxy.ProductOptionProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.ProductOption;



public class ProductOptionDAOImpl extends DAO implements ProductOptionDAO {

    private PreparedStatement sProductOptionById;

    public ProductOptionDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sProductOptionById = getConnection().prepareStatement("SELECT * FROM product_option WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductOptionDAO", ex);
        }
    }

    protected ProductOption createProductOption(ResultSet rs) throws SQLException {
        ProductOptionProxy p = new ProductOptionProxy(getDataLayer());
        p.setKey(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setAddictionalPrice(rs.getDouble("additional_price"));
        p.setVersion(rs.getLong("version"));
        
        // Adesso questo metodo esiste nel Proxy e l'errore sparirà!
        p.setClean(); 
        return p;
    }

    @Override
    public ProductOption getProductOptionById(int product_option_key) throws DataException {
        try {
            sProductOptionById.setInt(1, product_option_key);
            try (ResultSet rs = sProductOptionById.executeQuery()) {
                if (rs.next()) return createProductOption(rs);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getProductOptionById", ex);
        }
        return null;
    }

    @Override
    public List<ProductOption> getAllProductOptions() throws DataException {
        List<ProductOption> res = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM product_option")) {
            while (rs.next()) {
                res.add(createProductOption(rs));
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getAllProductOptions", ex);
        }
        return res;
    }

    @Override
    public void addProductOption(ProductOption option) throws DataException {
        // Implementazione per l'aggiunta
    }

    @Override
    public void updateProductOption(ProductOption option) throws DataException {
        // Implementazione per l'aggiornamento
    }

    @Override
    public void deleteProductOption(ProductOption option) throws DataException {
        // Implementazione per la cancellazione
    }
}