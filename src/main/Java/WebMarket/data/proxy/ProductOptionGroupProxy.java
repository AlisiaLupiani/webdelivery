package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.ProductOptionGroup;
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

    // Metodi per la gestione dello stato del proxy richiesti dai DAO
    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }
}