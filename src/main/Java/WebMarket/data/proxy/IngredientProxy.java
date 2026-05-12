package WebMarket.data.proxy;

import WebMarket.data.FoodDAO;
import framework.data.DataLayer;
import model.Food;
import model.modelImpl.IngredientImpl;

public class IngredientProxy extends IngredientImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    protected Integer idFoodNascosto;


    public IngredientProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
    }

    public void setIdFoodNascosto(Integer id) {
        this.idFoodNascosto = id;
    }


   @Override
    public void setId(Integer id) {
        super.setId(id);
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

        if(food != null){
            this.idFoodNascosto = food.getKey();
        }else{
            this.idFoodNascosto = 0;
        }

        this.isDirty = true;
    }

    @Override
    public Food getFood() {
        if (super.getFood() == null && this.idFoodNascosto > 0) {
            try {

                FoodDAO foodDAO = (FoodDAO) dataLayer.getDAO(Food.class);
                super.setFood(foodDAO.getFoodById(this.idFoodNascosto)); 
                
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }
        return super.getFood();
    }

    @Override 
    public void setKey(Integer key) {
        super.setKey(key);
        this.isDirty = true;
    }

    @Override
    public void setVersion(long version) {
        super.setVersion(version);
        this.isDirty = true;
    }
    
    public boolean isModified() {
         return isDirty;
    }
    public void setClean() {
        this.isDirty = false;
    }
}