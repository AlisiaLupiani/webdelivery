package model.modelImpl;

import java.time.LocalDateTime;

import framework.data.DataItemImpl;
import model.LogOrderState;
import model.Order;
import model.OrderState;
import model.Staff;


public class LogOrderStateImpl extends DataItemImpl<Integer> implements LogOrderState {

    protected Integer key;
    protected Order order;
    protected Staff staff;
    protected OrderState stateFrom;
    protected OrderState stateTo;
    protected LocalDateTime dateTime;
    protected long version;

    public LogOrderStateImpl() {
        this.key = 0;
        this.order = null;
        this.staff = null;
        this.stateFrom = null;
        this.stateTo = null;
        this.dateTime = LocalDateTime.now();
        this.version = 0;
    }

    @Override
    public Integer getKey() { return key; }
    @Override
    public void setKey(Integer key) { this.key = key; }

    @Override
    public Integer getId() { return key; }
    @Override
    public void setId(Integer id) { this.key = id; }

    @Override
    public Order getOrder() { return order; }
    @Override
    public void setOrder(Order order) { this.order = order; }

    @Override
    public Staff getStaff() { return staff; }
    @Override
    public void setStaff(Staff staff) { this.staff = staff; }

    @Override
    public OrderState getStateFrom() { return stateFrom; }
    @Override
    public void setStateFrom(OrderState state) { this.stateFrom = state; }

    @Override
    public OrderState getStateTo() { return stateTo; }
    @Override
    public void setStateTo(OrderState state) { this.stateTo = state; }

    @Override
    public LocalDateTime getDateTime() { return dateTime; }
    @Override
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    @Override
    public long getVersion() { return version; }
    @Override
    public void setVersion(long version) { this.version = version; }
}