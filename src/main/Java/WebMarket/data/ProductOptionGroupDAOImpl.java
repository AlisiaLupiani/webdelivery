package WebMarket.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.ProductOptionGroup;
import model.Product;
import WebMarket.data.proxy.ProductOptionGroupProxy;

public class ProductOptionGroupDAOImpl extends DAO implements ProductOptionGroupDAO {

    private PreparedStatement sGroupById;

    // QUESTO È IL COSTRUTTORE CHE RISOLVE L'ERRORE "Implicit super constructor"
    public ProductOptionGroupDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sGroupById = getConnection().prepareStatement("SELECT * FROM product_option_group WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductOptionGroupDAO", ex);
        }
    }

    protected ProductOptionGroup createProductOptionGroup(ResultSet rs) throws SQLException {
        ProductOptionGroupProxy p = new ProductOptionGroupProxy(getDataLayer());
        p.setKey(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setVersion(rs.getLong("version"));
        
        // Questo richiederà setClean() nel proxy, che ti metto sotto se non lo hai!
        p.setClean(); 
        return p;
    }

    @Override
    public ProductOptionGroup getProductOptionGroupById(int group_key) throws DataException {
        try {
            sGroupById.setInt(1, group_key);
            try (ResultSet rs = sGroupById.executeQuery()) {
                if (rs.next()) return createProductOptionGroup(rs);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getProductOptionGroupById", ex);
        }
        return null;
    }

    @Override
    public List<ProductOptionGroup> getAllProductOptionGroups() throws DataException {
        List<ProductOptionGroup> res = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM product_option_group")) {
            while (rs.next()) res.add(createProductOptionGroup(rs));
        } catch (SQLException ex) {
            throw new DataException("Errore getAllProductOptionGroups", ex);
        }
        return res;
    }

    @Override
    public List<ProductOptionGroup> getProductOptionGroupsByProduct(Product product) throws DataException {
        List<ProductOptionGroup> res = new ArrayList<>();
        // Inserire la query corretta per caricare i gruppi di un prodotto
        return res;
    }

    @Override public void addProductOptionGroup(ProductOptionGroup group) throws DataException {}
    @Override public void updateProductOptionGroup(ProductOptionGroup group) throws DataException {}
    @Override public void deleteProductOptionGroup(ProductOptionGroup group) throws DataException {}
}