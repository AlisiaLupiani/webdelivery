package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.Product;
import model.Ingredient;
import model.modelImpl.ProductImpl;
import java.util.List;

public class ProductProxy extends ProductImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ProductProxy(DataLayer dl) {
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
    public void setPrice(Double price) {
        super.setPrice(price);
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

    // Questo è il metodo che mancava!
    public void setClean() {
        this.isDirty = false;
    }
}