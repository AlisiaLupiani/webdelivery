package WebMarket.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Ingredient;
import model.Product;
import WebMarket.data.proxy.IngredientProxy;

public class IngredientDAOImpl extends DAO implements IngredientDAO {

    private PreparedStatement sIngredientById;

    public IngredientDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Prepariamo la query base
            sIngredientById = getConnection().prepareStatement("SELECT * FROM ingredient WHERE id=?");
        } catch (SQLException ex) {
            throw new DataException("Errore durante l'inizializzazione del DAO Ingredient", ex);
        }
    }

    // Metodo helper per convertire il ResultSet in un oggetto Java
    protected Ingredient createIngredient(ResultSet rs) throws SQLException {
        IngredientProxy i = new IngredientProxy(getDataLayer());
        i.setKey(rs.getInt("id"));
        i.setQuantity(rs.getString("quantity"));
        // Il food sarà caricato in lazy loading dal proxy
        i.setVersion(rs.getLong("version"));
        return i;
    }

    @Override
    public Ingredient getIngredientById(int ingredient_key) throws DataException {
        try {
            sIngredientById.setInt(1, ingredient_key);
            try (ResultSet rs = sIngredientById.executeQuery()) {
                if (rs.next()) {
                    return createIngredient(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero Ingredient per ID", ex);
        }
        return null;
    }

    @Override
    public List<Ingredient> getAllIngredients() throws DataException {
        List<Ingredient> res = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM ingredient")) {
            while (rs.next()) {
                res.add(createIngredient(rs));
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero di tutti gli Ingredient", ex);
        }
        return res;
    }

    @Override
    public List<Ingredient> getIngredientsByProduct(Product product) throws DataException {
        List<Ingredient> res = new ArrayList<>();
        // Inserisci qui la query che collega il prodotto agli ingredienti
        // Esempio: SELECT i.* FROM ingredient i JOIN product_ingredient pi ON i.id = pi.ingredient_id WHERE pi.product_id = ?
        return res;
    }

    // Metodi CRUD da implementare
    @Override
    public void addIngredient(Ingredient ingredient) throws DataException {
        // Logica per inserire un ingrediente
    }

    @Override
    public void updateIngredient(Ingredient ingredient) throws DataException {
        // Logica per aggiornare un ingrediente
    }

    @Override
    public void deleteIngredient(Ingredient ingredient) throws DataException {
        // Logica per eliminare un ingrediente
    }
}