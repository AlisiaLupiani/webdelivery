package model.modelImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import model.Client;
import model.Order;
import model.OrderState;
import model.PaymentMethod;
import model.Product;

public class OrderImpl implements Order {

    private Integer id;
    private Client client;
    private LocalDate date;
    private Double price;
    private LocalTime deliveryTime;
    private OrderState orderState;
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private List<Product> products;

    // Costruttore vuoto per il DAO
    public OrderImpl() {
        this.id = 0;
        this.client = null;
        this.date = LocalDate.now();
        this.price = 0.0;
        this.deliveryTime = LocalTime.now();
        this.orderState = null;
        this.paymentMethod = null;
        this.deliveryAddress = "";
        this.products = new ArrayList<>();
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
    public Client getClient() {
        return this.client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public Double getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public LocalTime getDeliveryTime() {
        return this.deliveryTime;
    }

    @Override
    public void setDeliveryTime(LocalTime time) {
        this.deliveryTime = time;
    }

    @Override
    public OrderState getOrderState() {
        return this.orderState;
    }

    @Override
    public void setOrderState(OrderState state) {
        this.orderState = state;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    @Override
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public List<Product> getProducts() {
        return this.products;
    }

    @Override
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}