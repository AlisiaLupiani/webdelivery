package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.Food;


public interface FoodDAO {
    Food getFoodById(Food food) throws DataException;
    Food getFoodByName(Food food) throws DataException;
    List<Food> getAllFoods() throws DataException;

    void addFood(Food food) throws DataException;
    void updateFood(Food food) throws DataException;
    void deleteFood(Food food) throws DataException;


}
