package WebMarket.data.dao;

import framework.data.DataException;
import model.CartItem;

import java.util.List;

public interface CartItemDAO {

    List<CartItem> getItemsByCartId(int cartId) throws DataException;

    CartItem addItem(int cartId, int productId, int quantity, double prezzoUnitario) throws DataException;

    void addOptionToItem(int cartItemId, int optionId) throws DataException;

    void updateQuantity(int cartItemId, int quantity) throws DataException;

    void removeItem(int cartItemId) throws DataException;

    void clearCart(int cartId) throws DataException;
}