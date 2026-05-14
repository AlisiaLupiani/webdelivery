package WebMarket.data;

import java.util.List;
import framework.data.DataException;
import model.Ingredient;
import model.Product;

public interface IngredientDAO {

    Ingredient getIngredientById(int ingredient_key) throws DataException;

    List<Ingredient> getAllIngredients() throws DataException;

    
    List<Ingredient> getIngredientsByProduct(Product product) throws DataException;

    
    void addIngredient(Ingredient ingredient) throws DataException;
    
    void updateIngredient(Ingredient ingredient) throws DataException;
    
    void deleteIngredient(Ingredient ingredient) throws DataException;
}