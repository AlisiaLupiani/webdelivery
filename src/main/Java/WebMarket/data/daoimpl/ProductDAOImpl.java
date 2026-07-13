package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private PreparedStatement sAddIngredientToProduct;
    private PreparedStatement sRemoveIngredientFromProduct;
    private PreparedStatement sOptionsByOrderProduct;
    private PreparedStatement sOptionsByProduct;
    private PreparedStatement sAddOptionToProduct;
    private PreparedStatement sRemoveOptionFromProduct;

    private static final String TABLE = "PRODOTTO";

    public ProductDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sProductById = getConnection().prepareStatement(
                    "SELECT * FROM " + TABLE + " WHERE ID=?"
            );

            sProductByOrder = getConnection().prepareStatement(
                    "SELECT p.* FROM " + TABLE + " p " +
                    "INNER JOIN ORDINE_PRODOTTO op ON p.ID = op.PRODOTTO_ID " +
                    "WHERE op.ORDINE_ID = ?"
            );

            sAllProducts = getConnection().prepareStatement(
                    "SELECT * FROM " + TABLE + " ORDER BY CATEGORIA, NOME"
            );

            sAddProduct = getConnection().prepareStatement(
                    "INSERT INTO " + TABLE + " " +
                    "(NOME, DESCRIZIONE, PREZZO, PROCEDURA, TEMPO_PREPARAZIONE, IMMAGINE, CATEGORIA, VERSION) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            sUpdateProduct = getConnection().prepareStatement(
                    "UPDATE " + TABLE + " SET " +
                    "NOME=?, PREZZO=?, DESCRIZIONE=?, PROCEDURA=?, TEMPO_PREPARAZIONE=?, IMMAGINE=?, CATEGORIA=?, VERSION=? " +
                    "WHERE ID=? AND VERSION=?"
            );

            sDeleteProduct = getConnection().prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE ID=?"
            );

            sOrderProductDetails = getConnection().prepareStatement(
                    "SELECT p.*, op.QUANTITA AS ORDINE_QUANTITA " +
                    "FROM ORDINE_PRODOTTO op " +
                    "JOIN PRODOTTO p ON op.PRODOTTO_ID = p.ID " +
                    "WHERE op.ORDINE_ID = ? " +
                    "ORDER BY p.NOME"
            );

            sIngredientsByProduct = getConnection().prepareStatement(
                    "SELECT i.ID, i.NOME, pi.QUANTITA " +
                    "FROM PRODOTTO_INGREDIENTE pi " +
                    "JOIN INGREDIENTE i ON pi.INGREDIENTE_ID = i.ID " +
                    "WHERE pi.PRODOTTO_ID = ? " +
                    "ORDER BY i.NOME"
            );

            sAddIngredientToProduct = getConnection().prepareStatement(
                    "INSERT INTO PRODOTTO_INGREDIENTE (PRODOTTO_ID, INGREDIENTE_ID, QUANTITA) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE QUANTITA = VALUES(QUANTITA)"
            );

            sRemoveIngredientFromProduct = getConnection().prepareStatement(
                    "DELETE FROM PRODOTTO_INGREDIENTE WHERE PRODOTTO_ID = ? AND INGREDIENTE_ID = ?"
            );

            sOptionsByOrderProduct = getConnection().prepareStatement(
                    "SELECT c.ID, c.NOME, c.DESCRIZIONE, c.PREZZO, c.IS_DEFAULT, g.NOME AS GRUPPO " +
                    "FROM ORDINE_PRODOTTO_CARATTERISTICA opc " +
                    "JOIN CARATTERISTICA c ON opc.CARATTERISTICA_ID = c.ID " +
                    "JOIN GRUPPO g ON c.GRUPPO_ID = g.ID " +
                    "WHERE opc.ORDINE_ID = ? AND opc.PRODOTTO_ID = ? " +
                    "ORDER BY g.NOME, c.NOME"
            );

            sOptionsByProduct = getConnection().prepareStatement(
                    "SELECT c.ID, c.NOME, c.DESCRIZIONE, c.PREZZO, c.IS_DEFAULT, g.NOME AS GRUPPO " +
                    "FROM PRODOTTO_CARATTERISTICA pc " +
                    "JOIN CARATTERISTICA c ON pc.CARATTERISTICA_ID = c.ID " +
                    "JOIN GRUPPO g ON c.GRUPPO_ID = g.ID " +
                    "WHERE pc.PRODOTTO_ID = ? " +
                    "ORDER BY g.NOME, c.NOME"
            );

            sAddOptionToProduct = getConnection().prepareStatement(
                    "INSERT IGNORE INTO PRODOTTO_CARATTERISTICA (PRODOTTO_ID, CARATTERISTICA_ID) VALUES (?, ?)"
            );

            sRemoveOptionFromProduct = getConnection().prepareStatement(
                    "DELETE FROM PRODOTTO_CARATTERISTICA WHERE PRODOTTO_ID = ? AND CARATTERISTICA_ID = ?"
            );

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sProductById != null) sProductById.close();
            if (sProductByOrder != null) sProductByOrder.close();
            if (sAllProducts != null) sAllProducts.close();
            if (sAddProduct != null) sAddProduct.close();
            if (sUpdateProduct != null) sUpdateProduct.close();
            if (sDeleteProduct != null) sDeleteProduct.close();
            if (sOrderProductDetails != null) sOrderProductDetails.close();
            if (sIngredientsByProduct != null) sIngredientsByProduct.close();
            if (sAddIngredientToProduct != null) sAddIngredientToProduct.close();
            if (sRemoveIngredientFromProduct != null) sRemoveIngredientFromProduct.close();
            if (sOptionsByOrderProduct != null) sOptionsByOrderProduct.close();
            if (sOptionsByProduct != null) sOptionsByProduct.close();
            if (sAddOptionToProduct != null) sAddOptionToProduct.close();
            if (sRemoveOptionFromProduct != null) sRemoveOptionFromProduct.close();

            super.destroy();
        } catch (SQLException ex) {
            throw new DataException("Errore chiusura ProductDAO", ex);
        }
    }

    protected Product createProduct(ResultSet rs) throws SQLException {
        ProductProxy product = new ProductProxy(getDataLayer());

        product.setKey(rs.getInt("ID"));
        product.setName(rs.getString("NOME"));
        product.setDescription(rs.getString("DESCRIZIONE"));
        product.setPrice(rs.getDouble("PREZZO"));
        product.setProcedure(rs.getString("PROCEDURA"));
        product.setPreparationTime(rs.getInt("TEMPO_PREPARAZIONE"));
        product.setImage(rs.getString("IMMAGINE"));
        product.setCategory(rs.getString("CATEGORIA"));
        product.setVersion(rs.getLong("VERSION"));

        product.setClean();

        return product;
    }

    @Override
    public Product getProductById(int product_key) throws DataException {
        Product product = null;

        if (getDataLayer().getCache().has(Product.class, product_key)) {
            product = getDataLayer().getCache().get(Product.class, product_key);
        } else {
            try {
                sProductById.setInt(1, product_key);

                try (ResultSet rs = sProductById.executeQuery()) {
                    if (rs.next()) {
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
                    Product product;
                    int productId = rs.getInt("ID");

                    if (getDataLayer().getCache().has(Product.class, productId)) {
                        product = getDataLayer().getCache().get(Product.class, productId);
                    } else {
                        product = createProduct(rs);
                        getDataLayer().getCache().add(Product.class, product);
                    }

                    products.add(product);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore getProductsByOrder", e);
        }

        return products;
    }

    @Override
    public List<Product> getAllProducts() throws DataException {
        List<Product> result = new ArrayList<>();

        try (ResultSet rs = sAllProducts.executeQuery()) {
            while (rs.next()) {
                Product product;
                Integer id = rs.getInt("ID");

                if (getDataLayer().getCache().has(Product.class, id)) {
                    product = getDataLayer().getCache().get(Product.class, id);
                } else {
                    product = createProduct(rs);
                    getDataLayer().getCache().add(Product.class, product);
                }

                result.add(product);
            }
        } catch (SQLException e) {
            throw new DataException("Errore getAllProducts", e);
        }

        return result;
    }

    @Override
    public void addProduct(Product product) throws DataException {
        try {
            sAddProduct.setString(1, product.getName());
            sAddProduct.setString(2, product.getDescription());
            sAddProduct.setDouble(3, product.getPrice());
            sAddProduct.setString(4, product.getProcedure());
            sAddProduct.setInt(5, product.getPreparationTime());
            sAddProduct.setString(6, product.getImage());
            sAddProduct.setString(7, product.getCategory());

            long initialVersion = 1;
            sAddProduct.setLong(8, initialVersion);

            if (sAddProduct.executeUpdate() == 1) {
                try (ResultSet resultSet = sAddProduct.getGeneratedKeys()) {
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

    @Override
    public void updateProduct(Product product) throws DataException {
        try {
            sUpdateProduct.setString(1, product.getName());
            sUpdateProduct.setDouble(2, product.getPrice());
            sUpdateProduct.setString(3, product.getDescription());
            sUpdateProduct.setString(4, product.getProcedure());
            sUpdateProduct.setInt(5, product.getPreparationTime());
            sUpdateProduct.setString(6, product.getImage());
            sUpdateProduct.setString(7, product.getCategory());

            long currentVersion = product.getVersion();
            long nextVersion = currentVersion + 1;

            sUpdateProduct.setLong(8, nextVersion);
            sUpdateProduct.setInt(9, product.getKey());
            sUpdateProduct.setLong(10, currentVersion);

            if (sUpdateProduct.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed: product modified by another process");
            }

            product.setVersion(nextVersion);
            getDataLayer().getCache().add(Product.class, product);

        } catch (SQLException e) {
            throw new DataException("Unable to update product", e);
        }
    }

    @Override
    public void deleteProduct(Product product) throws DataException {
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
                    Product product = createProduct(rs);

                    Map<String, Object> row = new HashMap<>();
                    row.put("prodotto", product);
                    row.put("quantita", rs.getInt("ORDINE_QUANTITA"));
                    row.put("ingredienti", getIngredientsByProductId(product.getKey()));
                    row.put("personalizzazioni", getOptionsByOrderProduct(order.getKey(), product.getKey()));

                    result.add(row);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore recupero dettagli prodotti ordine", e);
        }

        return result;
    }

    @Override
    public List<Map<String, String>> getIngredientsByProductId(int productId) throws DataException {
        List<Map<String, String>> result = new ArrayList<>();

        try {
            sIngredientsByProduct.setInt(1, productId);

            try (ResultSet rs = sIngredientsByProduct.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> ingredient = new HashMap<>();
                    ingredient.put("id", String.valueOf(rs.getInt("ID")));
                    ingredient.put("nome", rs.getString("NOME"));
                    ingredient.put("quantita", rs.getString("QUANTITA"));
                    result.add(ingredient);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore recupero ingredienti prodotto", e);
        }

        return result;
    }

    @Override
    public void addIngredientToProduct(int productId, int ingredientId, String quantity) throws DataException {
        try {
            sAddIngredientToProduct.setInt(1, productId);
            sAddIngredientToProduct.setInt(2, ingredientId);
            sAddIngredientToProduct.setString(3, quantity);
            sAddIngredientToProduct.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Errore collegamento ingrediente prodotto", e);
        }
    }

    @Override
    public void removeIngredientFromProduct(int productId, int ingredientId) throws DataException {
        try {
            sRemoveIngredientFromProduct.setInt(1, productId);
            sRemoveIngredientFromProduct.setInt(2, ingredientId);
            sRemoveIngredientFromProduct.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Errore rimozione ingrediente prodotto", e);
        }
    }

    @Override
    public List<Map<String, Object>> getOptionsByProductId(int productId) throws DataException {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            sOptionsByProduct.setInt(1, productId);

            try (ResultSet rs = sOptionsByProduct.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> option = new HashMap<>();
                    option.put("id", rs.getInt("ID"));
                    option.put("nome", rs.getString("NOME"));
                    option.put("descrizione", rs.getString("DESCRIZIONE"));
                    option.put("prezzo", rs.getDouble("PREZZO"));
                    option.put("default", rs.getBoolean("IS_DEFAULT"));
                    option.put("gruppo", rs.getString("GRUPPO"));
                    result.add(option);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore recupero caratteristiche prodotto", e);
        }

        return result;
    }

    @Override
    public void addOptionToProduct(int productId, int optionId) throws DataException {
        try {
            sAddOptionToProduct.setInt(1, productId);
            sAddOptionToProduct.setInt(2, optionId);
            sAddOptionToProduct.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Errore collegamento caratteristica prodotto", e);
        }
    }

    @Override
    public void removeOptionFromProduct(int productId, int optionId) throws DataException {
        try {
            sRemoveOptionFromProduct.setInt(1, productId);
            sRemoveOptionFromProduct.setInt(2, optionId);
            sRemoveOptionFromProduct.executeUpdate();
        } catch (SQLException e) {
            throw new DataException("Errore rimozione caratteristica prodotto", e);
        }
    }

    private List<Map<String, Object>> getOptionsByOrderProduct(int orderId, int productId) throws DataException {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            sOptionsByOrderProduct.setInt(1, orderId);
            sOptionsByOrderProduct.setInt(2, productId);

            try (ResultSet rs = sOptionsByOrderProduct.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> option = new HashMap<>();
                    option.put("id", rs.getInt("ID"));
                    option.put("nome", rs.getString("NOME"));
                    option.put("descrizione", rs.getString("DESCRIZIONE"));
                    option.put("prezzo", rs.getDouble("PREZZO"));
                    option.put("default", rs.getBoolean("IS_DEFAULT"));
                    option.put("gruppo", rs.getString("GRUPPO"));
                    result.add(option);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore recupero personalizzazioni ordine", e);
        }

        return result;
    }
}