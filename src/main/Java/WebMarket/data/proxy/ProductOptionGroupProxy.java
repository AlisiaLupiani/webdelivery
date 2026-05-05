package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.modelImpl.ProductOptionGroupImpl;





public class ProductOptionGroupProxy extends ProductOptionGroupImpl{

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ProductOptionGroupProxy(DataLayer dl) {
        this.dataLayer = dl;
        this.isDirty = false;
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
        this.isDirty = true;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.isDirty = true;
    }

    
    
    

}

