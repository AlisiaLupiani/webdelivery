package model.impl;

import model.Food;
import model.Ingredient;

public class IngredientImpl implements Ingredient {

    private Integer id;
    private Food food;
    private String quantity;

    // Costruttore vuoto per il DAO
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

    /**
     * Implementato come 'setFoot' per rispettare l'interfaccia inviata.
     * Si consiglia di correggere l'interfaccia in 'setFood'.
     */
    @Override
    public void setFoot(Food food) {
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