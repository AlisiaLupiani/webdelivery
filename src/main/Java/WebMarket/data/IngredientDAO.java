package WebMarket.data;

import java.util.List;
import framework.data.DataException;
import model.Ingredient;
import model.Product;

public interface IngredientDAO {

    // Ricerca per ID (chiave primaria)
    Ingredient getIngredientById(int ingredient_key) throws DataException;

    // Recupera tutti gli ingredienti
    List<Ingredient> getAllIngredients() throws DataException;

    // Recupera gli ingredienti associati a un prodotto
    List<Ingredient> getIngredientsByProduct(Product product) throws DataException;

    // Operazioni di scrittura
    void addIngredient(Ingredient ingredient) throws DataException;
    
    void updateIngredient(Ingredient ingredient) throws DataException;
    
    void deleteIngredient(Ingredient ingredient) throws DataException;
}