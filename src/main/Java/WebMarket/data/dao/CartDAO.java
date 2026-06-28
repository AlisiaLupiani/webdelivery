package WebMarket.data.dao;

import framework.data.DataException;
import model.Cart;

public interface CartDAO {

    Cart getActiveCartByUserId(int userId) throws DataException;

    Cart createCart(int userId) throws DataException;

    Cart getOrCreateActiveCart(int userId) throws DataException;

    void closeCart(int cartId) throws DataException;

    void deleteCart(int cartId) throws DataException;
}