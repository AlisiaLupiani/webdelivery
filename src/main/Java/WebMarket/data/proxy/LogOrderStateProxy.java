package WebMarket.data.proxy;

import java.time.LocalDateTime;

import framework.data.DataLayer;
import model.Order;
import model.OrderState;
import model.Staff;
import model.modelImpl.LogOrderStateImpl;

public class LogOrderStateProxy extends LogOrderStateImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public LogOrderStateProxy(DataLayer dl) {
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
    public void setOrder(Order order) {
        super.setOrder(order);
        this.isDirty = true;
    }

    @Override
    public void setStaff(Staff staff) {
        super.setStaff(staff);
        this.isDirty = true;
    }

    @Override
    public void setStateFrom(OrderState state) {
        super.setStateFrom(state);
        this.isDirty = true;
    }

    @Override
    public void setStateTo(OrderState state) {
        super.setStateTo(state);
        this.isDirty = true;
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
        super.setDateTime(dateTime);
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


    public boolean isDirty() {
        return isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }
}