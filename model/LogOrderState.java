package model;

import java.time.LocalDateTime;

public interface LogOrderState {
	
	
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

