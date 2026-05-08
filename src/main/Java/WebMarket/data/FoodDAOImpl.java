package WebMarket.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Food;
import model.Ingredient;
import WebMarket.data.proxy.FoodProxy;

public class FoodDAOImpl extends DAO implements FoodDAO {

    private PreparedStatement sFoodById;
    private PreparedStatement sFoodByName;

    public FoodDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Prepariamo le query SQL
            sFoodById = getConnection().prepareStatement("SELECT * FROM food WHERE id=?");
            sFoodByName = getConnection().prepareStatement("SELECT * FROM food WHERE name=?");
        } catch (SQLException ex) {
            throw new DataException("Errore durante l'inizializzazione del DAO Food", ex);
        }
    }

    // Metodo di supporto per creare l'oggetto dai risultati del DB
    protected Food createFood(ResultSet rs) throws SQLException {
        FoodProxy f = new FoodProxy(getDataLayer());
        f.setKey(rs.getInt("id"));
        f.setName(rs.getString("name"));
        f.setVersion(rs.getLong("version"));
        return f;
    }

    @Override
    public Food getFoodById(int food_key) throws DataException {
        try {
            sFoodById.setInt(1, food_key);
            try (ResultSet rs = sFoodById.executeQuery()) {
                if (rs.next()) {
                    return createFood(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero Food per ID", ex);
        }
        return null;
    }

    @Override
    public Food getFoodByName(String name) throws DataException {
        try {
            sFoodByName.setString(1, name);
            try (ResultSet rs = sFoodByName.executeQuery()) {
                if (rs.next()) {
                    return createFood(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero Food per nome", ex);
        }
        return null;
    }

    @Override
    public List<Food> getAllFoods() throws DataException {
        List<Food> res = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM food")) {
            while (rs.next()) {
                res.add(createFood(rs));
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero di tutti i Food", ex);
        }
        return res;
    }

    @Override
    public Food getFoodByIngredient(Ingredient ingredient) throws DataException {
        if (ingredient.getFood() != null) {
            return getFoodById(ingredient.getFood().getKey());
        }
        return null;
    }

    @Override public void addFood(Food food) throws DataException { /* Implementa insert */ }
    @Override public void updateFood(Food food) throws DataException { /* Implementa update */ }
    @Override public void deleteFood(Food food) throws DataException { /* Implementa delete */ }
}