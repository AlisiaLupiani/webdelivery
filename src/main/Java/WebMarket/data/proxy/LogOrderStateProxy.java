package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.LogOrderState;
import model.Order;
import model.Staff;
import model.OrderState;
import model.modelImpl.LogOrderStateImpl;
import java.time.LocalDateTime;

public class LogOrderStateProxy extends LogOrderStateImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public LogOrderStateProxy(DataLayer dl) {
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