package model.modelImpl;

import model.LogOrderState;
import model.Order;
import model.OrderState;
import model.Staff;
import java.time.LocalDateTime;

public class LogOrderStateImpl implements LogOrderState {

    private Integer id;
    private Order order;
    private Staff staff;
    private OrderState stateFrom;
    private OrderState stateTo;
    private LocalDateTime dateTime;

    // Costruttore vuoto per il DAO
    public LogOrderStateImpl() {
        this.id = 0;
        this.order = null;
        this.staff = null;
        this.stateFrom = null;
        this.stateTo = null;
        this.dateTime = LocalDateTime.now();
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Order getOrder() {
        return this.order;
    }

    @Override
    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public Staff getStaff() {
        return this.staff;
    }

    @Override
    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Override
    public OrderState getStateFrom() {
        return this.stateFrom;
    }

    @Override
    public void setStateFrom(OrderState state) {
        this.stateFrom = state;
    }

    @Override
    public OrderState getStateTo() {
        return this.stateTo;
    }

    @Override
    public void setStateTo(OrderState state) {
        this.stateTo = state;
    }

    @Override
    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}