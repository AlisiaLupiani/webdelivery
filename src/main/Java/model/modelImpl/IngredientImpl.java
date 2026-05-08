package model.modelImpl;

import model.Ingredient;
import model.Food;

public class IngredientImpl implements Ingredient {

    protected Integer key;
    protected String quantity;
    protected Food food;
    protected long version;

    public IngredientImpl() {
        this.key = 0;
        this.quantity = "";
        this.food = null;
        this.version = 0;
    }

    @Override
    public Integer getKey() { return key; }
    @Override
    public void setKey(Integer key) { this.key = key; }

    @Override
    public Integer getId() { return key; }
    @Override
    public void setId(Integer id) { this.key = id; }

    @Override
    public String getQuantity() { return quantity; }
    @Override
    public void setQuantity(String quantity) { this.quantity = quantity; }

    @Override
    public Food getFood() { return food; }
    @Override
    public void setFood(Food food) { this.food = food; }

    @Override
    public long getVersion() { return version; }
    @Override
    public void setVersion(long version) { this.version = version; }
}