package WebMarket.data.dao;

import java.util.List;

import framework.data.DataException;
import model.Food;

public interface FoodDAO {

    // Cerchiamo tramite il numero ID (chiave primaria)
    Food getFoodById(int food_key) throws DataException;

    // Cerchiamo tramite il nome (Stringa)
    Food getFoodByName(String name) throws DataException;

    List<Food> getAllFoods() throws DataException;

    void addFood(Food food) throws DataException;

    void updateFood(Food food) throws DataException;

    void deleteFood(Food food) throws DataException;
}