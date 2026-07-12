package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.proxy.ProductProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Order;
import model.Product;



public class ProductDAOImpl extends DAO implements ProductDAO {

    private PreparedStatement sProductById;
    private PreparedStatement sProductByOrder;
    private PreparedStatement sAllProducts;
    private PreparedStatement sAddProduct;
    private PreparedStatement sUpdateProduct;
    private PreparedStatement sDeleteProduct;
    private PreparedStatement sOrderProductDetails;
private PreparedStatement sIngredientsByProduct;
    
    private static final String TABLE = "PRODOTTO";


    public ProductDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init(); 
            
            sProductById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID=?");
            
            sProductByOrder = getConnection().prepareStatement(
                "SELECT p.* FROM " + TABLE + " p INNER JOIN ORDINE_PRODOTTO op ON p.ID = op.PRODOTTO_ID WHERE op.ORDINE_ID = ?");
            
            sAllProducts = getConnection().prepareStatement("SELECT * FROM " + TABLE);

            sAddProduct = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + " (NOME, DESCRIZIONE, PREZZO, PROCEDURA, TEMPO_PREPARAZIONE, IMMAGINE, VERSION) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            sUpdateProduct = getConnection().prepareStatement(
                "UPDATE " + TABLE + " SET NOME=?, PREZZO=?, DESCRIZIONE=?, PROCEDURA=?, TEMPO_PREPARAZIONE=?, IMMAGINE=?, VERSION=? WHERE ID=? AND VERSION=?");
            
            sDeleteProduct = getConnection().prepareStatement("DELETE FROM " + TABLE + " WHERE ID=?");

            sOrderProductDetails = getConnection().prepareStatement(
    "SELECT p.*, op.QUANTITA AS ORDINE_QUANTITA " +
    "FROM ORDINE_PRODOTTO op " +
    "JOIN PRODOTTO p ON op.PRODOTTO_ID = p.ID " +
    "WHERE op.ORDINE_ID = ? " +
    "ORDER BY p.NOME"
);

sIngredientsByProduct = getConnection().prepareStatement(
    "SELECT i.NOME, pi.QUANTITA " +
    "FROM PRODOTTO_INGREDIENTE pi " +
    "JOIN INGREDIENTE i ON pi.INGREDIENTE_ID = i.ID " +
    "WHERE pi.PRODOTTO_ID = ? " +
    "ORDER BY i.NOME"
);
        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try{
            if(sProductById != null) sProductById.close();
            if(sProductByOrder != null) sProductByOrder.close();
            if(sAllProducts != null) sAllProducts.close();
            if(sAddProduct != null) sAddProduct.close();
            if(sUpdateProduct != null) sUpdateProduct.close();
            if(sDeleteProduct != null) sDeleteProduct.close();
            if (sOrderProductDetails != null) sOrderProductDetails.close();
if (sIngredientsByProduct != null) sIngredientsByProduct.close();

            super.destroy();
        }catch(SQLException ex){
            throw new DataException("Errore chiusura ProductDAO", ex);
        }
    }


    protected Product createProduct(ResultSet rs) throws SQLException {
        ProductProxy p = new ProductProxy(getDataLayer());
        p.setKey(rs.getInt("ID"));
        p.setName(rs.getString("NOME"));
        p.setDescription(rs.getString("DESCRIZIONE"));
        p.setPrice(rs.getDouble("PREZZO"));
        p.setProcedure(rs.getString("PROCEDURA"));
        p.setPreparationTime(rs.getInt("TEMPO_PREPARAZIONE"));
        p.setImage(rs.getString("IMMAGINE"));
        p.setCategory(rs.getString("CATEGORIA"));
        p.setVersion(rs.getLong("VERSION"));

        p.setClean();

        return p;
    
    }

    @Override
    public Product getProductById(int product_key) throws DataException {
         Product product = null;
        if(getDataLayer().getCache().has(Product.class, product_key)){
            product = getDataLayer().getCache().get(Product.class, product_key);
        }else{
            try {
                sProductById.setInt(1, product_key);
                try (ResultSet rs = sProductById.executeQuery()){;
                    if(rs.next()){
                        product = createProduct(rs);
                        getDataLayer().getCache().add(Product.class, product);
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Errore getProductById", e);

            }
        }
        return product;
    }

@Override
    public List<Product> getProductsByOrder(Order order) throws DataException { 
        List<Product> products = new ArrayList<>();
        try {
            sProductByOrder.setInt(1, order.getKey()); 
            
            try (ResultSet rs = sProductByOrder.executeQuery()) {
                while (rs.next()) {
                    Product p = null;
                    int productId = rs.getInt("ID");
                    
                    if(getDataLayer().getCache().has(Product.class, productId)){
                        p = getDataLayer().getCache().get(Product.class, productId);
                    }else{
                        p = createProduct(rs);
                        getDataLayer().getCache().add(Product.class, p);
                    }
                    products.add(p);
                }
            }
        } catch(SQLException e){
            throw new DataException("Errore getProductsByOrder", e);
        }
        return products;
    }

    @Override
    public List<Product> getAllProducts() throws DataException {
        List<Product> res = new ArrayList<>(); 
        try(ResultSet rs = sAllProducts.executeQuery()){
            while(rs.next()){ 
                Product product;
                Integer id = rs.getInt("ID");
                if(getDataLayer().getCache().has(Product.class, id)){
                    product = getDataLayer().getCache().get(Product.class, id);
                }else{
                    product = createProduct(rs);
                    getDataLayer().getCache().add(Product.class, product);
                }
                res.add(product);
            }
            
        } catch (SQLException e) {
            throw new DataException("Errore getAllProducts", e);
        }
        
        return res;
    }

    @Override public void addProduct(Product product) throws DataException {
        try {
            sAddProduct.setString(1, product.getName());
            sAddProduct.setString(2, product.getDescription());
            sAddProduct.setDouble(3, product.getPrice());
            sAddProduct.setString(4, product.getProcedure());
            sAddProduct.setInt(5, product.getPreparationTime());
            sAddProduct.setString(6, product.getImage());

            long initialVersion = 1;
            sAddProduct.setLong(7, initialVersion);

            if (sAddProduct.executeUpdate() == 1) {
                try(ResultSet resultSet = sAddProduct.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        Integer newKey = resultSet.getInt(1);
                        product.setKey(newKey);
                        product.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(Product.class, product);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to add product", e);
        }

    }


    @Override public void updateProduct(Product product) throws DataException {
        try {
            sUpdateProduct.setString(1, product.getName());
            sUpdateProduct.setDouble(2, product.getPrice());
            sUpdateProduct.setString(3, product.getDescription());
            sUpdateProduct.setString(4, product.getProcedure());
            sUpdateProduct.setInt(5, product.getPreparationTime());
            sUpdateProduct.setString(6, product.getImage());

            long currentVersion = product.getVersion();
            long nextVersion = currentVersion + 1;
            sUpdateProduct.setLong(7, nextVersion);
            sUpdateProduct.setInt(8, product.getKey());
            sUpdateProduct.setLong(9, currentVersion);

            if (sUpdateProduct.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed: product modified by another process");
            } else {
                product.setVersion(nextVersion);
            }        
        } catch(SQLException e) {
            throw new DataException("Unable to update product", e);
        }

    }
    @Override public void deleteProduct(Product product) throws DataException {
        try {
            sDeleteProduct.setInt(1, product.getKey());
            if (sDeleteProduct.executeUpdate() > 0) {
                getDataLayer().getCache().delete(Product.class, product.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Unable to delete product", e);
        }
    
    }

    @Override
public List<Map<String, Object>> getOrderProductDetails(Order order) throws DataException {
    List<Map<String, Object>> result = new ArrayList<>();

    try {
        sOrderProductDetails.setInt(1, order.getKey());

        try (ResultSet rs = sOrderProductDetails.executeQuery()) {
            while (rs.next()) {
                Product prodotto = createProduct(rs);

                Map<String, Object> riga = new HashMap<>();
                riga.put("prodotto", prodotto);
                riga.put("quantita", rs.getInt("ORDINE_QUANTITA"));
                riga.put("ingredienti", getIngredientsByProductId(prodotto.getKey()));

                result.add(riga);
            }
        }
    } catch (SQLException e) {
        throw new DataException("Errore recupero dettagli prodotti ordine", e);
    }

    return result;
}

private List<Map<String, String>> getIngredientsByProductId(int productId) throws SQLException {
    List<Map<String, String>> result = new ArrayList<>();

    sIngredientsByProduct.setInt(1, productId);

    try (ResultSet rs = sIngredientsByProduct.executeQuery()) {
        while (rs.next()) {
            Map<String, String> ingrediente = new HashMap<>();
            ingrediente.put("nome", rs.getString("NOME"));
            ingrediente.put("quantita", rs.getString("QUANTITA"));
            result.add(ingrediente);
        }
    }

    return result;
}
}
