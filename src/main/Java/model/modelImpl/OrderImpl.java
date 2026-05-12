package model.modelImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import framework.data.DataItemImpl;
import model.Client;
import model.Order;
import model.OrderState;
import model.PaymentMethod;
import model.Product;

public class OrderImpl extends DataItemImpl<Integer> implements Order {

    protected Integer key;
    protected Client client;
    protected LocalDate date;
    protected Double price;
    protected LocalTime deliveryTime;
    protected OrderState orderState;
    protected PaymentMethod paymentMethod;
    protected String deliveryAddress;
    protected List<Product> products;
    

    public OrderImpl() {
        this.key = 0;
        this.client = null;
        this.date = LocalDate.now();
        this.price = 0.0;
        this.deliveryTime = LocalTime.now();
        this.orderState = null;
        this.paymentMethod = null;
        this.deliveryAddress = "";
        this.products = new ArrayList<>();
        
    }

    // Metodi per la Chiave (DataItem)
    @Override
    public Integer getKey() { return key; }
    @Override
    public void setKey(Integer key) { this.key = key; }

    // Metodi per l'ID (Alias di Key)
    @Override
    public Integer getId() { return key; }
    @Override
    public void setId(Integer id) { this.key = id; }

    @Override
    public Client getClient() { return client; }
    @Override
    public void setClient(Client client) { this.client = client; }

    @Override
    public LocalDate getDate() { return date; }
    @Override
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public Double getPrice() { return price; }
    @Override
    public void setPrice(Double price) { this.price = price; }

    @Override
    public LocalTime getDeliveryTime() { return deliveryTime; }
    @Override
    public void setDeliveryTime(LocalTime time) { this.deliveryTime = time; }

    @Override
    public OrderState getOrderState() { return orderState; }
    @Override
    public void setOrderState(OrderState state) { this.orderState = state; }

    @Override
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    @Override
    public String getDeliveryAddress() { return deliveryAddress; }
    @Override
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    @Override
    public List<Product> getProducts() { return products; }
    @Override
    public void setProducts(List<Product> products) { this.products = products; }

    
}