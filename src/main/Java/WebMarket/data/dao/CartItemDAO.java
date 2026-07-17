package WebMarket.data.dao;

import framework.data.DataException;
import model.CartItem;

import java.util.List;

public interface CartItemDAO {

    List<CartItem> getItemsByCartId(int cartId) throws DataException;

    CartItem addItem(int cartId, int productId, int quantity, double prezzoUnitario) throws DataException;

    void addOptionToItem(int cartItemId, int optionId) throws DataException;

    boolean updateQuantity(int cartItemId, int cartId, int quantity) throws DataException;

    boolean removeItem(int cartItemId, int cartId) throws DataException;

    void clearCart(int cartId) throws DataException;
}
