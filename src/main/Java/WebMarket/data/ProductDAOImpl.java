package WebMarket.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Product;
import WebMarket.data.proxy.ProductProxy;

public class ProductDAOImpl extends DAO implements ProductDAO {

    private PreparedStatement sProductById;

    public ProductDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init(); // Deve stare dentro il try perché può lanciare SQLException
            sProductById = getConnection().prepareStatement("SELECT * FROM product WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductDAO", ex);
        }
    }

    protected Product createProduct(ResultSet rs) throws SQLException {
        ProductProxy p = new ProductProxy(getDataLayer());
        p.setKey(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setVersion(rs.getLong("version"));
        
        // Diciamo al proxy che è appena nato dal DB, quindi non è "sporco"
        p.setClean(); 
        return p;
    }

    @Override
    public Product getProductById(int product_key) throws DataException {
        try {
            sProductById.setInt(1, product_key);
            try (ResultSet rs = sProductById.executeQuery()) {
                if (rs.next()) return createProduct(rs);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getProductById", ex);
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() throws DataException {
        List<Product> res = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM product")) {
            while (rs.next()) {
                res.add(createProduct(rs));
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getAllProducts", ex);
        }
        return res;
    }

    @Override public void addProduct(Product product) throws DataException {}
    @Override public void updateProduct(Product product) throws DataException {}
    @Override public void deleteProduct(Product product) throws DataException {}
}