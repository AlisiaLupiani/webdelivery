package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import WebMarket.data.dao.CartDAO;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Cart;
import model.modelImpl.CartImpl;

public class CartDAOImpl extends DAO implements CartDAO {

    private PreparedStatement sActiveCartByUserId;
    private PreparedStatement sCreateCart;
    private PreparedStatement sCloseCart;
    private PreparedStatement sDeleteCart;

    private static final String TABLE = "CARRELLO";

    public CartDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sActiveCartByUserId = getConnection().prepareStatement(
                    "SELECT * FROM " + TABLE + " WHERE UTENTE_ID=? AND STATO='ATTIVO' LIMIT 1"
            );

            sCreateCart = getConnection().prepareStatement(
                    "INSERT INTO " + TABLE + " (UTENTE_ID, STATO, VERSION) VALUES (?, 'ATTIVO', 1)",
                    Statement.RETURN_GENERATED_KEYS
            );

            sCloseCart = getConnection().prepareStatement(
                    "UPDATE " + TABLE + " SET STATO='CONVERTITO_IN_ORDINE', VERSION=VERSION+1 WHERE ID=?"
            );

            sDeleteCart = getConnection().prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE ID=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione CartDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sActiveCartByUserId != null) sActiveCartByUserId.close();
            if (sCreateCart != null) sCreateCart.close();
            if (sCloseCart != null) sCloseCart.close();
            if (sDeleteCart != null) sDeleteCart.close();

            super.destroy();

        } catch (SQLException ex) {
            throw new DataException("Errore chiusura CartDAO", ex);
        }
    }

    protected Cart createCartFromResultSet(ResultSet rs) throws SQLException {
        CartImpl cart = new CartImpl();

        cart.setKey(rs.getInt("ID"));
        cart.setUserId(rs.getInt("UTENTE_ID"));
        cart.setStato(rs.getString("STATO"));
        cart.setVersion(rs.getInt("VERSION"));

        return cart;
    }

    @Override
    public Cart getActiveCartByUserId(int userId) throws DataException {
        Cart cart = null;

        try {
            sActiveCartByUserId.setInt(1, userId);

            try (ResultSet rs = sActiveCartByUserId.executeQuery()) {
                if (rs.next()) {
                    cart = createCartFromResultSet(rs);
                }
            }

        } catch (SQLException ex) {
            throw new DataException("Errore getActiveCartByUserId", ex);
        }

        return cart;
    }

    @Override
    public Cart createCart(int userId) throws DataException {
        Cart cart = null;

        try {
            sCreateCart.setInt(1, userId);

            if (sCreateCart.executeUpdate() == 1) {
                try (ResultSet rs = sCreateCart.getGeneratedKeys()) {
                    if (rs.next()) {
                        CartImpl newCart = new CartImpl();

                        newCart.setKey(rs.getInt(1));
                        newCart.setUserId(userId);
                        newCart.setStato("ATTIVO");
                        newCart.setVersion(1);

                        cart = newCart;
                    }
                }
            }

        } catch (SQLException ex) {
            throw new DataException("Errore createCart", ex);
        }

        return cart;
    }

    @Override
    public Cart getOrCreateActiveCart(int userId) throws DataException {
        Cart cart = getActiveCartByUserId(userId);

        if (cart == null) {
            cart = createCart(userId);
        }

        return cart;
    }

    @Override
    public void closeCart(int cartId) throws DataException {
        try {
            sCloseCart.setInt(1, cartId);
            sCloseCart.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Errore closeCart", ex);
        }
    }

    @Override
    public void deleteCart(int cartId) throws DataException {
        try {
            sDeleteCart.setInt(1, cartId);
            sDeleteCart.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Errore deleteCart", ex);
        }
    }
}