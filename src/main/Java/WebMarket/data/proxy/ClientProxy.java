package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.modelImpl.ClientImpl;


public class ClientProxy extends ClientImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public ClientProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
    }

    @Override
    public void setPhone(Integer id) {
        super.setId(id);
        this.isDirty = true;
    }

    @Override 
    public void setAddres(String address){
        super.setAddress(address);
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
