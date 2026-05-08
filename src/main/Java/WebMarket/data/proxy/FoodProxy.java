package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.Food;
import model.modelImpl.FoodImpl;

public class FoodProxy extends FoodImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public FoodProxy(DataLayer dl) {
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
    public void setName(String name) {
        super.setName(name);
        this.isDirty = true;
    }

    @Override
    public void setVersion(long version) {
        super.setVersion(version);
        this.isDirty = true;
    }

    // Metodi di utilità per il proxy
    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public void setClean() {
        this.isDirty = false;
    }
}