package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.IngredientDAO;
import WebMarket.data.proxy.IngredientProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Food;
import model.Ingredient;



public class IngredientDAOImpl extends DAO implements IngredientDAO {

    private PreparedStatement sIngredientById;
    private PreparedStatement sAllIngredients;
    private PreparedStatement sIngredientsByFood;
    private PreparedStatement sAddIngredient;
    private PreparedStatement sUpdateIngredient;
    private PreparedStatement sDeleteIngredient;

    private static final String TABLE = "INGREDIENTE";

    public IngredientDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sIngredientById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
            sAllIngredients = getConnection().prepareStatement("SELECT * FROM " + TABLE);
            
            // Query per recuperare gli ingredienti di un prodotto tramite la tabella di join
            sIngredientsByFood = getConnection().prepareStatement("SELECT * FROM " + TABLE + "WHERE CIBO_ID = ?");

            sAddIngredient = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + " (NOME, VERSION) VALUES (?, ?)", 
                Statement.RETURN_GENERATED_KEYS);

            sUpdateIngredient = getConnection().prepareStatement(
                "UPDATE " + TABLE + " SET NOME = ?, VERSION = ? WHERE ID = ? AND VERSION = ?");

            sDeleteIngredient = getConnection().prepareStatement("DELETE FROM " + TABLE + " WHERE ID = ?");

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione IngredientDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sIngredientById != null) sIngredientById.close();
            if (sAllIngredients != null) sAllIngredients.close();
            if (sIngredientsByFood != null) sIngredientsByFood.close();
            if (sAddIngredient != null) sAddIngredient.close();
            if (sUpdateIngredient != null) sUpdateIngredient.close();
            if (sDeleteIngredient != null) sDeleteIngredient.close();
            super.destroy();
        } catch (SQLException ex) {
            throw new DataException("Errore chiusura IngredientDAO", ex);
        }
    }

    protected Ingredient createIngredient(ResultSet rs) throws SQLException {
        IngredientProxy i = new IngredientProxy(getDataLayer());
        i.setKey(rs.getInt("ID"));
        i.setName(rs.getString("NOME"));
        i.setVersion(rs.getLong("VERSION"));
        
        i.setClean();
        return i;
    }

    @Override
    public Ingredient getIngredientById(int ingredient_key) throws DataException {

        Ingredient ingredient = null;

        if (getDataLayer().getCache().has(Ingredient.class, ingredient_key)) {

            ingredient = getDataLayer().getCache().get(Ingredient.class, ingredient_key);
        }
        try {
            sIngredientById.setInt(1, ingredient_key);
            try (ResultSet rs = sIngredientById.executeQuery()) {
                if (rs.next()) {
                    ingredient = createIngredient(rs);
                    getDataLayer().getCache().add(Ingredient.class, ingredient);
                    return ingredient;
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore recupero ingrediente per ID", e);
        }
        return ingredient;
    }

    @Override
    public List<Ingredient> getAllIngredients() throws DataException {
        List<Ingredient> result = new ArrayList<>();
        try (ResultSet rs = sAllIngredients.executeQuery()) {
            while (rs.next()) {
                result.add(createIngredient(rs));
            }
        } catch (SQLException ex) {
            throw new DataException("Errore recupero tutti gli ingredienti", ex);
        }
        return result;
    }

    @Override
    public List<Ingredient> getIngredientsByFood(Food food) throws DataException {
        List<Ingredient> result = new ArrayList<>();
        try {
            sIngredientsByFood.setInt(1, food.getKey());
            try (ResultSet rs = sIngredientsByFood.executeQuery()) {
                while (rs.next()) {
                    result.add(createIngredient(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore recupero ingredienti per prodotto", ex);
        }
        return result;
    }

    @Override
    public void addIngredient(Ingredient ingredient) throws DataException {
        try {
            sAddIngredient.setString(1, ingredient.getName());
            
            long initialVersion = 1;
            sAddIngredient.setLong(2, initialVersion);

            if (sAddIngredient.executeUpdate() == 1) {
                try (ResultSet rs = sAddIngredient.getGeneratedKeys()) {
                    if (rs.next()) {
                        ingredient.setKey(rs.getInt(1));
                        ingredient.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(Ingredient.class, ingredient);
            }
        } catch (SQLException e) {
            throw new DataException("Errore inserimento ingrediente", e);
        }
    }

    @Override
    public void updateIngredient(Ingredient ingredient) throws DataException {
        try {
            long currentVersion = ingredient.getVersion();
            long nextVersion = currentVersion + 1;

            sUpdateIngredient.setString(1, ingredient.getName());
            sUpdateIngredient.setLong(2, nextVersion);
            sUpdateIngredient.setInt(3, ingredient.getKey());
            sUpdateIngredient.setLong(4, currentVersion);

            if (sUpdateIngredient.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed su Ingredient");
            } else {
                ingredient.setVersion(nextVersion);
            }
        } catch (SQLException e) {
            throw new DataException("Errore aggiornamento ingrediente", e);
        }
    }

    @Override
    public void deleteIngredient(Ingredient ingredient) throws DataException {
        try {
            sDeleteIngredient.setInt(1, ingredient.getKey());
            if (sDeleteIngredient.executeUpdate() > 0) {
                getDataLayer().getCache().delete(Ingredient.class, ingredient.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Errore eliminazione ingrediente", e);
        }
    }
}