package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.modelImpl.UserImpl;


public class UserProxy extends UserImpl{

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public UserProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
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
    public void setSurname(String surname) {
        super.setSurname(surname);
        this.isDirty = true;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.isDirty = true;
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
        this.isDirty = true;
    }



}
