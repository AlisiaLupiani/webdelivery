package WebMarket.data;

import java.sql.SQLException;

import javax.sql.DataSource;

import WebMarket.data.dao.CartDAO;
import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.dao.ConsumationDAO;
import WebMarket.data.dao.FoodDAO;
import WebMarket.data.dao.IngredientDAO;
import WebMarket.data.dao.LogOrderStateDAO;
import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.ProductOptionDAO;
import WebMarket.data.dao.ProductOptionGroupDAO;
import WebMarket.data.dao.UserDAO;
import WebMarket.data.daoimpl.CartDAOImpl;
import WebMarket.data.daoimpl.CartItemDAOImpl;
import WebMarket.data.daoimpl.ConsumationDAOImpl;
import WebMarket.data.daoimpl.FoodDAOImpl;
import WebMarket.data.daoimpl.IngredientDAOImpl;
import WebMarket.data.daoimpl.LogOrderStateDAOImpl;
import WebMarket.data.daoimpl.OrderDAOImpl;
import WebMarket.data.daoimpl.ProductDAOImpl;
import WebMarket.data.daoimpl.ProductOptionDAOImpl;
import WebMarket.data.daoimpl.ProductOptionGroupDAOImpl;
import WebMarket.data.daoimpl.UserDAOImpl;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Cart;
import model.CartItem;
import model.Consumation;
import model.Food;
import model.Ingredient;
import model.LogOrderState;
import model.Order;
import model.Product;
import model.ProductOption;
import model.ProductOptionGroup;
import model.User;

public class WebDeliveryDataLayer extends DataLayer {

    public WebDeliveryDataLayer(DataSource datasource) throws SQLException {
        super(datasource);
    }

    @Override
    public void init() throws DataException {
        registerDAO(Cart.class, new CartDAOImpl(this));
        registerDAO(CartItem.class, new CartItemDAOImpl(this));
        registerDAO(Consumation.class, new ConsumationDAOImpl(this));
        registerDAO(Food.class, new FoodDAOImpl(this));
        registerDAO(Ingredient.class, new IngredientDAOImpl(this));
        registerDAO(LogOrderState.class, new LogOrderStateDAOImpl(this));
        registerDAO(Order.class, new OrderDAOImpl(this));
        registerDAO(Product.class, new ProductDAOImpl(this));
        registerDAO(ProductOption.class, new ProductOptionDAOImpl(this));
        registerDAO(ProductOptionGroup.class, new ProductOptionGroupDAOImpl(this));
        registerDAO(User.class, new UserDAOImpl(this));
    }

    public CartDAO getCartDAO() {
        return (CartDAO) getDAO(Cart.class);
    }

    public CartItemDAO getCartItemDAO() {
        return (CartItemDAO) getDAO(CartItem.class);
    }

    public ConsumationDAO getConsumationDAO() {
        return (ConsumationDAO) getDAO(Consumation.class);
    }

    public FoodDAO getFoodDAO() {
        return (FoodDAO) getDAO(Food.class);
    }

    public IngredientDAO getIngredientDAO() {
        return (IngredientDAO) getDAO(Ingredient.class);
    }

    public LogOrderStateDAO getLogOrderStateDAO() {
        return (LogOrderStateDAO) getDAO(LogOrderState.class);
    }

    public OrderDAO getOrderDAO() {
        return (OrderDAO) getDAO(Order.class);
    }

    public ProductDAO getProductDAO() {
        return (ProductDAO) getDAO(Product.class);
    }

    public ProductOptionDAO getProductOptionDAO() {
        return (ProductOptionDAO) getDAO(ProductOption.class);
    }

    public ProductOptionGroupDAO getProductOptionGroupDAO() {
        return (ProductOptionGroupDAO) getDAO(ProductOptionGroup.class);
    }

    public UserDAO getUserDAO() {
        return (UserDAO) getDAO(User.class);
    }
}
