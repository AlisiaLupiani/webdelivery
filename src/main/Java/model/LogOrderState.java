package model;

import java.time.LocalDateTime;

import framework.data.DataItem;

// Estendiamo DataItem per permettere al DAO di funzionare
public interface LogOrderState extends DataItem<Integer> {
    
    public Integer getId();
    public void setId(Integer id);
    
    public Order getOrder();
    public void setOrder(Order order);
    
    public Staff getStaff();
    public void setStaff(Staff staff);
    
    public OrderState getStateFrom();
    public void setStateFrom(OrderState state);
    
    public OrderState getStateTo();
    public void setStateTo(OrderState state);
    
    public LocalDateTime getDateTime();
    public void setDateTime(LocalDateTime dateTime);

}