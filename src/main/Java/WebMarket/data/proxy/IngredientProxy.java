package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.Ingredient;
import model.Food;
import model.modelImpl.IngredientImpl;
import WebMarket.data.FoodDAO;

public class IngredientProxy extends IngredientImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public IngredientProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.isDirty = true;
    }

    @Override
    public void setQuantity(String quantity) {
        super.setQuantity(quantity);
        this.isDirty = true;
    }

    @Override
    public void setFood(Food food) {
        super.setFood(food);
        this.isDirty = true;
    }

    // Risolve l'errore del caricamento automatico (Lazy Loading)
    @Override
    public Food getFood() {
        if (super.getFood() == null) {
            try {
                // Qui carichiamo il cibo dal database solo quando serve
                FoodDAO foodDAO = (FoodDAO) dataLayer.getDAO(Food.class);
                super.setFood(foodDAO.getFoodByIngredient(this));
            } catch (Exception e) {
                return null;
            }
        }
        return super.getFood();
    }

    public void setDirty(boolean dirty) { this.isDirty = dirty; }
    public boolean isDirty() { return isDirty; }
    public void setClean() { this.isDirty = false; }
}