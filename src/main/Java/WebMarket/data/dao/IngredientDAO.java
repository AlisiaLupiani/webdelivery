package WebMarket.data.dao;

import java.util.List;

import framework.data.DataException;
import model.Ingredient;

public interface IngredientDAO {

    Ingredient getIngredientById(int ingredient_key) throws DataException;

    List<Ingredient> getAllIngredients() throws DataException;

    
    void addIngredient(Ingredient ingredient) throws DataException;
    
    void updateIngredient(Ingredient ingredient) throws DataException;
    
    void deleteIngredient(Ingredient ingredient) throws DataException;
}
