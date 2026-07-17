package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.ProductOptionDAO;
import WebMarket.data.proxy.ProductOptionProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.ProductOption;
import model.ProductOptionGroup;



public class ProductOptionDAOImpl extends DAO implements ProductOptionDAO {

    private PreparedStatement sProductOptionById;
    private PreparedStatement sProductOptionByProductOptionGroup;
    private PreparedStatement sProductOptionsByProduct;
    private PreparedStatement sAllProductOptions;
    private PreparedStatement sAddProductOption;
    private PreparedStatement sUpdateProductOption;
    private PreparedStatement sDeleteProductOption;
    private static final String TABLE = "CARATTERISTICA";


    public ProductOptionDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sProductOptionById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID=?");
            sProductOptionByProductOptionGroup = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE GRUPPO_ID= ?");
            sProductOptionsByProduct = getConnection().prepareStatement(
                    "SELECT c.* FROM " + TABLE + " c " +
                    "JOIN PRODOTTO_CARATTERISTICA pc ON pc.CARATTERISTICA_ID = c.ID " +
                    "WHERE pc.PRODOTTO_ID = ? ORDER BY c.GRUPPO_ID, c.ID"
            );
            sAllProductOptions = getConnection().prepareStatement("SELECT * FROM " + TABLE);

            sAddProductOption = getConnection().prepareStatement("INSERT INTO " + TABLE + " (NOME,DESCRIZIONE,PREZZO,IS_DEFAULT,GRUPPO_ID, VERSION) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            
            // Query UPDATE corretta: NOME, DESCRIZIONE, PREZZO, IS_DEFAULT, GRUPPO_ID, VERSION (6 campi)
            // Filtra per ID e VERSION (2 campi)
            sUpdateProductOption = getConnection().prepareStatement("UPDATE " + TABLE + " SET NOME=?,DESCRIZIONE=?,PREZZO=?,IS_DEFAULT=?,GRUPPO_ID=?, VERSION=? WHERE ID=? AND VERSION=?");
            
            sDeleteProductOption = getConnection().prepareStatement("DELETE FROM " + TABLE + " WHERE ID=? AND VERSION=?" );

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductOptionDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sProductOptionById != null) sProductOptionById.close();
            if (sProductOptionByProductOptionGroup != null) sProductOptionByProductOptionGroup.close();
            if (sProductOptionsByProduct != null) sProductOptionsByProduct.close();
            if (sAllProductOptions != null) sAllProductOptions.close();
            if (sAddProductOption != null) sAddProductOption.close();
            if (sUpdateProductOption != null) sUpdateProductOption.close();
            if (sDeleteProductOption != null) sDeleteProductOption.close();
            super.destroy();

        } catch (SQLException ex) {
            throw new DataException("Errore chiusura ProductOptionDAO", ex);
        }
    }

    protected ProductOption createProductOption(ResultSet rs) throws SQLException {
        ProductOptionProxy p = new ProductOptionProxy(getDataLayer());
        p.setKey(rs.getInt("ID"));
        p.setName(rs.getString("NOME"));
        p.setDescription(rs.getString("DESCRIZIONE"));
        p.setDefault(rs.getBoolean("IS_DEFAULT"));
        p.setAddictionalPrice(rs.getDouble("PREZZO"));
        
        int idGroup = rs.getInt("GRUPPO_ID");
        if(idGroup > 0){
            p.setIdProductOptionGroup(idGroup);
        }

        p.setVersion(rs.getLong("VERSION"));
        
        
        p.setClean(); 
        return p;
    }

    @Override
    public ProductOption getProductOptionById(int id) throws DataException {
        ProductOption option = null;

        if (getDataLayer().getCache().has(ProductOption.class, id)) {
            option = getDataLayer().getCache().get(ProductOption.class, id);
        } else {
                 try {
                    sProductOptionById.setInt(1, id);
                    try (ResultSet rs = sProductOptionById.executeQuery()) {
                    if (rs.next()) {
                        option = createProductOption(rs);
                        getDataLayer().getCache().add(ProductOption.class, option);
                    }
                }
                } catch (SQLException ex) {
                    throw new DataException("Errore getProductOptionById", ex);
                  }
            }  
        return option;
    }

    @Override
    public List<ProductOption> getProductOptionsByProductOptionGroup(ProductOptionGroup group) throws DataException {
        List<ProductOption> res = new ArrayList<>();
        try {
            sProductOptionByProductOptionGroup.setInt(1, group.getKey());
            try (ResultSet rs = sProductOptionByProductOptionGroup.executeQuery()) {
                while (rs.next()) {
                    ProductOption option;
                    Integer id = rs.getInt("ID");
                    // Controlliamo se la singola opzione è già in cache
                    if (getDataLayer().getCache().has(ProductOption.class, id)) {
                        option = getDataLayer().getCache().get(ProductOption.class, id);
                    } else {
                        option = createProductOption(rs);
                        getDataLayer().getCache().add(ProductOption.class, option);
                    }
                    res.add(option);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore getProductOptionsByProductOptionGroup", e);
        }
        return res;
    }

    @Override
    public List<ProductOption> getProductOptionsByProduct(int productId) throws DataException {
        List<ProductOption> result = new ArrayList<>();

        try {
            sProductOptionsByProduct.setInt(1, productId);

            try (ResultSet rs = sProductOptionsByProduct.executeQuery()) {
                while (rs.next()) {
                    int optionId = rs.getInt("ID");
                    ProductOption option;

                    if (getDataLayer().getCache().has(ProductOption.class, optionId)) {
                        option = getDataLayer().getCache().get(ProductOption.class, optionId);
                    } else {
                        option = createProductOption(rs);
                        getDataLayer().getCache().add(ProductOption.class, option);
                    }

                    result.add(option);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore recupero caratteristiche per prodotto", ex);
        }

        return result;
    }


    @Override
    public List<ProductOption> getAllProductOptions() throws DataException {
        List<ProductOption> res = new ArrayList<>();
        try (ResultSet rs = sAllProductOptions.executeQuery()) {
            while (rs.next()) {
                ProductOption option;
                Integer id = rs.getInt("ID");
                if(getDataLayer().getCache().has(ProductOption.class, id)){
                    option = getDataLayer().getCache().get(ProductOption.class, id);
                }else {
                    option = createProductOption(rs);
                    getDataLayer().getCache().add(ProductOption.class, option);
                }
                res.add(option);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getAllProductOptions", ex);
        }
        return res;
    }


    @Override
    public void addProductOption(ProductOption option) throws DataException {
        try {
            sAddProductOption.setString(1, option.getName());
            sAddProductOption.setString(2, option.getDescription());
            sAddProductOption.setDouble(3, option.getAddictionalPrice());
            sAddProductOption.setBoolean(4, option.isDefault());
            sAddProductOption.setInt(5, option.getProductOptionGroup().getKey());

            long initialVersion = 1;
            sAddProductOption.setLong(6, initialVersion);

            if (sAddProductOption.executeUpdate() == 1) {
                try(ResultSet rs = sAddProductOption.getGeneratedKeys()){
                    if(rs.next()){
                        option.setKey(rs.getInt(1));
                        option.setVersion(initialVersion);
                        
                    }
                }
                getDataLayer().getCache().add(ProductOption.class, option);
            }    
        } catch (SQLException e) {
            throw new DataException("Errore addProductOption", e);
        }
    }

    @Override
    public void updateProductOption(ProductOption option) throws DataException {
        try {
            sUpdateProductOption.setString(1, option.getName());
            sUpdateProductOption.setString(2, option.getDescription());
            sUpdateProductOption.setDouble(3, option.getAddictionalPrice());
            sUpdateProductOption.setBoolean(4, option.isDefault());
            sUpdateProductOption.setInt(5, option.getProductOptionGroup().getKey());

            long currentVersion = option.getVersion();
            sUpdateProductOption.setLong(6, currentVersion + 1);
            sUpdateProductOption.setInt(7, option.getKey());
            sUpdateProductOption.setLong(8, currentVersion);

            if (sUpdateProductOption.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed: product option modified by another process");
            }
            option.setVersion(currentVersion + 1);
            getDataLayer().getCache().add(ProductOption.class, option);
              
        } catch (SQLException e) {
            throw new DataException("Errore updateProductOption", e);
        }
    }
@Override
    public void deleteProductOption(ProductOption option) throws DataException {
        try {
            sDeleteProductOption.setInt(1, option.getKey());
            sDeleteProductOption.setLong(2, option.getVersion()); // <--- AGGIUNTO IL SECONDO PARAMETRO!
            
            if(sDeleteProductOption.executeUpdate() == 1){
                getDataLayer().getCache().delete(ProductOption.class, option.getKey());
            }      
        } catch (SQLException e) {
            throw new DataException("Errore deleteProductOption", e);
        }
    }
}
