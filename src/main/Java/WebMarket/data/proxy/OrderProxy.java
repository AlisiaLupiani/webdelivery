package WebMarket.data.proxy;

import framework.data.DataLayer;
import model.Order;
import model.Client;
import model.OrderState;
import model.PaymentMethod;
import model.Product;
import model.modelImpl.OrderImpl;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class OrderProxy extends OrderImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    public OrderProxy(DataLayer dl) {
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
    public void setClient(Client client) {
        super.setClient(client);
        this.isDirty = true;
    }

    @Override
    public void setDate(LocalDate date) {
        super.setDate(date);
        this.isDirty = true;
    }

    @Override
    public void setPrice(Double price) {
        super.setPrice(price);
        this.isDirty = true;
    }

    @Override
    public void setDeliveryTime(LocalTime time) {
        super.setDeliveryTime(time);
        this.isDirty = true;
    }

    @Override
    public void setOrderState(OrderState state) {
        super.setOrderState(state);
        this.isDirty = true;
    }

    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        super.setPaymentMethod(paymentMethod);
        this.isDirty = true;
    }

    @Override
    public void setDeliveryAddress(String deliveryAddress) {
        super.setDeliveryAddress(deliveryAddress);
        this.isDirty = true;
    }

    @Override
    public void setProducts(List<Product> products) {
        super.setProducts(products);
        this.isDirty = true;
    }

    @Override
    public void setVersion(long version) {
        super.setVersion(version);
        this.isDirty = true;
    }

    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }
}