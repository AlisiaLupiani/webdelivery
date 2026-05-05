package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.modelImpl.ProductOptionImpl;

public class ProductOptionProxy extends ProductOptionImpl{

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ProductOptionProxy(DataLayer dataLayer){
        super();
        this.dataLayer = dataLayer;
        this.isDirty = true;
    }
    
    @Override
    public void setName(String name){
        super.setName(name);
        isDirty = true;
    }
    
    @Override
    public void setDescription(String description){
        super.setDescription(description);
        isDirty = true;
    }
    
    @Override
    public void setAddictionalPrice(double price){
        super.setAddictionalPrice(price);
        isDirty = true;
    }
    
}
