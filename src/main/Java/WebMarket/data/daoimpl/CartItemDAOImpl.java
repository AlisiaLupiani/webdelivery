package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.proxy.ProductProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.CartItem;
import model.Product;
import model.modelImpl.CartItemImpl;

public class CartItemDAOImpl extends DAO implements CartItemDAO {

    private PreparedStatement sItemsByCartId;
    private PreparedStatement sAddItem;
    private PreparedStatement sAddOptionToItem;
    private PreparedStatement sUpdateQuantity;
    private PreparedStatement sRemoveItem;
    private PreparedStatement sClearCart;

    private static final String TABLE = "CARRELLO_PRODOTTO";

    public CartItemDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sItemsByCartId = getConnection().prepareStatement(
                    "SELECT " +
                            "cp.ID AS CART_ITEM_ID, " +
                            "cp.CARRELLO_ID, " +
                            "cp.PRODOTTO_ID, " +
                            "cp.QUANTITA, " +
                            "cp.PREZZO_UNITARIO, " +
                            "cp.VERSION AS CART_ITEM_VERSION, " +
                            "p.ID AS PRODUCT_ID, " +
                            "p.NOME, " +
                            "p.DESCRIZIONE, " +
                            "p.PREZZO, " +
                            "p.PROCEDURA, " +
                            "p.TEMPO_PREPARAZIONE, " +
                            "p.IMMAGINE, " +
                            "p.VERSION AS PRODUCT_VERSION " +
                            "FROM " + TABLE + " cp " +
                            "JOIN PRODOTTO p ON cp.PRODOTTO_ID = p.ID " +
                            "WHERE cp.CARRELLO_ID=?"
            );

            sAddItem = getConnection().prepareStatement(
                    "INSERT INTO " + TABLE + " (CARRELLO_ID, PRODOTTO_ID, QUANTITA, PREZZO_UNITARIO, VERSION) " +
                            "VALUES (?, ?, ?, ?, 1)",
                    Statement.RETURN_GENERATED_KEYS
            );

            sAddOptionToItem = getConnection().prepareStatement(
                    "INSERT INTO CARRELLO_PRODOTTO_CARATTERISTICA " +
                            "(CARRELLO_PRODOTTO_ID, CARATTERISTICA_ID) VALUES (?, ?)"
            );

            sUpdateQuantity = getConnection().prepareStatement(
                    "UPDATE " + TABLE + " SET QUANTITA=?, VERSION=VERSION+1 WHERE ID=?"
            );

            sRemoveItem = getConnection().prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE ID=?"
            );

            sClearCart = getConnection().prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE CARRELLO_ID=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione CartItemDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sItemsByCartId != null) sItemsByCartId.close();
            if (sAddItem != null) sAddItem.close();
            if (sAddOptionToItem != null) sAddOptionToItem.close();
            if (sUpdateQuantity != null) sUpdateQuantity.close();
            if (sRemoveItem != null) sRemoveItem.close();
            if (sClearCart != null) sClearCart.close();

            super.destroy();

        } catch (SQLException ex) {
            throw new DataException("Errore chiusura CartItemDAO", ex);
        }
    }

    private Product createProduct(ResultSet rs) throws SQLException {
        ProductProxy product = new ProductProxy(getDataLayer());

        product.setKey(rs.getInt("PRODUCT_ID"));
        product.setName(rs.getString("NOME"));
        product.setDescription(rs.getString("DESCRIZIONE"));
        product.setPrice(rs.getDouble("PREZZO"));
        product.setProcedure(rs.getString("PROCEDURA"));
        product.setPreparationTime(rs.getInt("TEMPO_PREPARAZIONE"));
        product.setImage(rs.getString("IMMAGINE"));
        product.setVersion(rs.getLong("PRODUCT_VERSION"));

        product.setClean();

        return product;
    }

    private CartItem createCartItem(ResultSet rs) throws SQLException {
        CartItemImpl item = new CartItemImpl();

        Product prodotto = createProduct(rs);

        item.setKey(rs.getInt("CART_ITEM_ID"));
        item.setCartId(rs.getInt("CARRELLO_ID"));
        item.setProductId(rs.getInt("PRODOTTO_ID"));
        item.setProdotto(prodotto);
        item.setQuantita(rs.getInt("QUANTITA"));
        item.setPrezzoUnitario(rs.getDouble("PREZZO_UNITARIO"));
        item.setVersion(rs.getInt("CART_ITEM_VERSION"));

        return item;
    }

    @Override
    public List<CartItem> getItemsByCartId(int cartId) throws DataException {
        List<CartItem> items = new ArrayList<>();

        try {
            sItemsByCartId.setInt(1, cartId);

            try (ResultSet rs = sItemsByCartId.executeQuery()) {
                while (rs.next()) {
                    items.add(createCartItem(rs));
                }
            }

        } catch (SQLException ex) {
            throw new DataException("Errore getItemsByCartId", ex);
        }

        return items;
    }

    @Override
    public CartItem addItem(int cartId, int productId, int quantity, double prezzoUnitario) throws DataException {
        CartItem item = null;

        try {
            sAddItem.setInt(1, cartId);
            sAddItem.setInt(2, productId);
            sAddItem.setInt(3, quantity);
            sAddItem.setDouble(4, prezzoUnitario);

            if (sAddItem.executeUpdate() == 1) {
                try (ResultSet rs = sAddItem.getGeneratedKeys()) {
                    if (rs.next()) {
                        CartItemImpl newItem = new CartItemImpl();

                        newItem.setKey(rs.getInt(1));
                        newItem.setCartId(cartId);
                        newItem.setProductId(productId);
                        newItem.setQuantita(quantity);
                        newItem.setPrezzoUnitario(prezzoUnitario);
                        newItem.setVersion(1);

                        item = newItem;
                    }
                }
            }

        } catch (SQLException ex) {
            throw new DataException("Errore addItem", ex);
        }

        return item;
    }

    @Override
    public void addOptionToItem(int cartItemId, int optionId) throws DataException {
        try {
            sAddOptionToItem.setInt(1, cartItemId);
            sAddOptionToItem.setInt(2, optionId);
            sAddOptionToItem.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Errore addOptionToItem", ex);
        }
    }

    @Override
    public void updateQuantity(int cartItemId, int quantity) throws DataException {
        try {
            sUpdateQuantity.setInt(1, quantity);
            sUpdateQuantity.setInt(2, cartItemId);
            sUpdateQuantity.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Errore updateQuantity", ex);
        }
    }

    @Override
    public void removeItem(int cartItemId) throws DataException {
        try {
            sRemoveItem.setInt(1, cartItemId);
            sRemoveItem.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Errore removeItem", ex);
        }
    }

    @Override
    public void clearCart(int cartId) throws DataException {
        try {
            sClearCart.setInt(1, cartId);
            sClearCart.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Errore clearCart", ex);
        }
    }
}