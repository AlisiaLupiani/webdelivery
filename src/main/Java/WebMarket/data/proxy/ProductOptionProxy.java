package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.ProductOption;
import model.ProductOptionGroup;
import model.modelImpl.ProductOptionImpl;

public class ProductOptionProxy extends ProductOptionImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ProductOptionProxy(DataLayer dl) {
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
    public void setDescription(String description) {
        super.setDescription(description);
        this.isDirty = true;
    }

    @Override
    public void setAddictionalPrice(Double price) {
        super.setAddictionalPrice(price);
        this.isDirty = true;
    }

    @Override
    public void setDefault(boolean df) {
        super.setDefault(df);
        this.isDirty = true;
    }

    @Override
    public void setProductOptionGroup(ProductOptionGroup productOptionGroup) {
        super.setProductOptionGroup(productOptionGroup);
        this.isDirty = true;
    }

    @Override
    public void setVersion(long version) {
        super.setVersion(version);
        this.isDirty = true;
    }

    // Metodi per la gestione dello stato del proxy
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