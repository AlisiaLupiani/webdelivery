package model.modelImpl;

import model.Food;
import model.Ingredient;

public class IngredientImpl implements Ingredient {

    private Integer id;
    private Food food;
    private String quantity;

    
    public IngredientImpl() {
        this.id = 0;
        this.food = null;
        this.quantity = "";
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Food getFood() {
        return this.food;
    }

   
    @Override
    public void setFood(Food food) {
        this.food = food;
    }

    @Override
    public String getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}