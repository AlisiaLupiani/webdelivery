package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.Consumation; // <--- Questo mancava!
import model.modelImpl.ConsumationImpl; // <--- Questo era scritto male!

// Ora Java troverà sia il padre (Impl) che l'interfaccia
public class ConsumationProxy extends ConsumationImpl implements Consumation {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ConsumationProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
    }

    // Usiamo setKey perché il framework DataItem usa "Key", non "Id"
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
    public void setPrice(Double price) {
        super.setPrice(price);
        this.isDirty = true;
    }

    // Metodi per il proxy
    public void setDirty() {
        this.isDirty = true;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }
}