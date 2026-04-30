package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.Ingredient;

public interface IngredientDAO {
    Ingredient getIngredientById(Ingredient ingredient) throws DataException;
    Ingredient getIngredientByFood(Ingredient ingredient) throws DataException;
    List<Ingredient> getAllIngredients() throws DataException;

    void addIngredient(Ingredient ingredient) throws DataException;
    void updateIngredient(Ingredient ingredient) throws DataException;
    void deleteIngredient(Ingredient ingredient) throws DataException;


}
