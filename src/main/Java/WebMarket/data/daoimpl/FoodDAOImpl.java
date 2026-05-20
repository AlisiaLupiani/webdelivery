package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.FoodDAO;
import WebMarket.data.proxy.FoodProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Food;


public class FoodDAOImpl extends DAO implements FoodDAO {

    private PreparedStatement sFoodById;
    private PreparedStatement sFoodByName;
    private PreparedStatement sAllFoods;
    private PreparedStatement sAddFood;
    private PreparedStatement sUpdateFood;
    private PreparedStatement sDeleteFood;

    // Utilizziamo la tabella CONSUMAZIONE come definito nello schema database
    private static final String TABLE = "CONSUMAZIONE";

    public FoodDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sFoodById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
            sFoodByName = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE NOME = ?");
            sAllFoods = getConnection().prepareStatement("SELECT * FROM " + TABLE);

            sAddFood = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + " (NOME, VERSION) VALUES (?,?)", 
                Statement.RETURN_GENERATED_KEYS);

            sUpdateFood = getConnection().prepareStatement(
                "UPDATE " + TABLE + " SET NOME = ?, VERSION = ? WHERE ID = ? AND VERSION = ?");

            sDeleteFood = getConnection().prepareStatement("DELETE FROM " + TABLE + " WHERE ID = ?");

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione FoodDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sFoodById != null) sFoodById.close();
            if (sFoodByName != null) sFoodByName.close();
            if (sAllFoods != null) sAllFoods.close();
            
            if (sAddFood != null) sAddFood.close();
            if (sUpdateFood != null) sUpdateFood.close();
            if (sDeleteFood != null) sDeleteFood.close();
            super.destroy();
        } catch (SQLException ex) {
            throw new DataException("Errore chiusura FoodDAO", ex);
        }
    }

    protected Food createFood(ResultSet rs) throws SQLException {
        FoodProxy f = new FoodProxy(getDataLayer());
        f.setKey(rs.getInt("ID"));
        f.setName(rs.getString("NOME"));
        f.setVersion(rs.getLong("VERSION"));
        
        f.setClean();
        return f;
    }

    @Override
    public Food getFoodById(int food_key) throws DataException {
        Food food = null;

        if (getDataLayer().getCache().has(Food.class, food_key)) {
            food = getDataLayer().getCache().get(Food.class, food_key);
        }else{
            try {
                sFoodById.setInt(1, food_key);
                try (ResultSet rs = sFoodById.executeQuery()) {
                    if (rs.next()) {
                        food = createFood(rs);
                        getDataLayer().getCache().add(Food.class, food);
                        
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Errore recupero Food per ID", e);
            }
        }
        return food;
    }

    @Override
    public Food getFoodByName(String name) throws DataException {
        Food food = null;
         if(getDataLayer().getCache().has(Food.class, name)){
            food = getDataLayer().getCache().get(Food.class, name);
         }else{

            try {
                sFoodByName.setString(1, name);
                try (ResultSet rs = sFoodByName.executeQuery()) {
                    if (rs.next()) {
                        food = createFood(rs);
                        getDataLayer().getCache().add(Food.class, food);
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Errore recupero Food per nome", e);
             }
        }
        return food;
    }

    @Override
    public List<Food> getAllFoods() throws DataException {
        List<Food> result = new ArrayList<>();
        try (ResultSet rs = sAllFoods.executeQuery()) {
            while (rs.next()) {
                Food food;
                Integer id = rs.getInt("ID");
                if (getDataLayer().getCache().has(Food.class, id)) {
                    food = getDataLayer().getCache().get(Food.class, id);
                } else {
                    food = createFood(rs);
                    getDataLayer().getCache().add(Food.class, food);
                }

                result.add(food);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore recupero lista Food", ex);
        }
        return result;
    }


    @Override
    public void addFood(Food food) throws DataException {
        try {
            sAddFood.setString(1, food.getName());
            
            long initialVersion = 1;
            sAddFood.setLong(4, initialVersion);

            if (sAddFood.executeUpdate() == 1) {
                try (ResultSet rs = sAddFood.getGeneratedKeys()) {
                    if (rs.next()) {
                        food.setKey(rs.getInt(1));
                        food.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(Food.class, food);
            }
        } catch (SQLException e) {
            throw new DataException("Errore inserimento Food", e);
        }
    }

    @Override
    public void updateFood(Food food) throws DataException {
        try {
            long currentVersion = food.getVersion();
            long nextVersion = currentVersion + 1;

            sUpdateFood.setString(1, food.getName());
            sUpdateFood.setLong(4, nextVersion);
            sUpdateFood.setInt(5, food.getKey());
            sUpdateFood.setLong(6, currentVersion);

            if (sUpdateFood.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed su Food");
            } else {
                food.setVersion(nextVersion);
            }
        } catch (SQLException e) {
            throw new DataException("Errore aggiornamento Food", e);
        }
    }

    @Override
    public void deleteFood(Food food) throws DataException {
        try {
            sDeleteFood.setInt(1, food.getKey());
            if (sDeleteFood.executeUpdate() > 0) {
                getDataLayer().getCache().delete(Food.class, food.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Errore eliminazione Food", e);
        }
    }
}