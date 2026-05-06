package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.modelImpl.ProductImpl;


public class ProductProxy extends ProductImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ProductProxy(DataLayer dataLayer) {
        this.dataLayer = dataLayer;
        this.isDirty = false;
    }

    public void setName(String name){
        super.setName(name);
        isDirty = true;
    }

    public void setDescription(String description){
        super.setDescription(description);
        isDirty = true;
    }

    public void setPrice (Double price){
        super.setPrice(price);
        isDirty = true;
    }

    public void setImage(String image){
        super.setImage(image);
        isDirty = true;
    }

    public void setPreparationTime(Integer preparationTime){
        super.setPreparationTime(preparationTime);
        isDirty = true;
    }

    public void setProcedure(String procedure){
        super.setProcedure(procedure);
        isDirty = true;
    }

    public void setIngredients(String ingredients){
        super.setIngredients(ingredients);
        isDirty = true;

    }


}
