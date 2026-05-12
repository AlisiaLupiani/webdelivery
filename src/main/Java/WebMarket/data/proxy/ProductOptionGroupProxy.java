package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.modelImpl.ProductOptionGroupImpl;

public class ProductOptionGroupProxy extends ProductOptionGroupImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ProductOptionGroupProxy(DataLayer dl) {
        super();
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