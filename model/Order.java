package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface Order {
	
	public Integer getId();
	public void setId(Integer id);
	
	public Client getClient();
	public void setClient(Client client);
	
	public LocalDate getDate();
	public void setDate(LocalDate date);
	
	public Double getPrice();
	public void setPrice(Double price);
	
	public LocalTime getDeliveryTime();
	public void setDeliveryTime(LocalTime time);
	
	public OrderState getOrderState();
	public void setOrderState(OrderState state);
	
	public PaymentMethod getPaymentMethod();
	public void setPaymentMethod(PaymentMethod paymentMethod);
	
	public String getDeliveryAddress();
	public void setDeliveryAddress(String deliveryAddress);
	
	public List<Product> getProducts();
	public void setProducts(List<Product> products);
	

}
