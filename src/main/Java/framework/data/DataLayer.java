package framework.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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


/**
 *
 * @author Giuseppe Della Penna
 */
public class DataLayer implements AutoCloseable {

    private final DataSource datasource;
    private Connection connection;
    private final Map<Class, DAO> daos;
    private final DataCache cache;

    public DataLayer(DataSource datasource) throws SQLException {
        super();
        this.datasource = datasource;
        this.connection = datasource.getConnection();
        this.daos = new HashMap<>();
        this.cache = new DataCache();
    }

    public void registerDAO(Class entityClass, DAO dao) throws DataException {
        daos.put(entityClass, dao);
        dao.init();
    }

    public DAO getDAO(Class entityClass) {
        return daos.get(entityClass);
    }

    public void init() throws DataException {
        this.registerDAO(model.Cart.class, new CartDAOImpl(this));
        this.registerDAO(model.CartItem.class, new CartItemDAOImpl(this));
        this.registerDAO(model.Consumation.class, new ConsumationDAOImpl(this));
        this.registerDAO(model.Food.class, new FoodDAOImpl(this));
        this.registerDAO(model.Ingredient.class, new IngredientDAOImpl(this));
        this.registerDAO(model.LogOrderState.class, new LogOrderStateDAOImpl(this));
        this.registerDAO(model.Order.class, new OrderDAOImpl(this));
        this.registerDAO(model.Product.class, new ProductDAOImpl(this));
        this.registerDAO(model.ProductOption.class, new ProductOptionDAOImpl(this));
        this.registerDAO(model.ProductOptionGroup.class, new ProductOptionGroupDAOImpl(this));
        this.registerDAO(model.User.class, new UserDAOImpl(this));

    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ex) {
            //
        }
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public Connection getConnection() {
        return connection;
    }

    public DataCache getCache() {
        return cache;
    }

    //metodo dell'interfaccia AutoCloseable (permette di usare questa classe nei try-with-resources)
    //method from the Autocloseable interface (allows this class to be used in try-with-resources)
    @Override
    public void close() throws Exception {
        destroy();
    }
}
