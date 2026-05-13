package WebMarket.data.proxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import WebMarket.data.ProductDAO;
import framework.data.DataLayer;
import model.Client;
import model.OrderState;
import model.PaymentMethod;
import model.Product;
import model.modelImpl.OrderImpl;


public class OrderProxy extends OrderImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;
    protected Integer idUtenteNascosto;


    public OrderProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
        this.idUtenteNascosto = 0;
    }

    public void setIdUtenteNascosto(Integer id) {
        this.idUtenteNascosto = id;
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
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
    public void setProducts(List<Product> products) {
        super.setProducts(products);
        this.isDirty = true;
    }

    @Override 
    public List<Product> getProducts() {
        if(super.getProducts() == null){
            try {
                ProductDAO productDao = (ProductDAO) dataLayer.getDAO(Product.class);

                List<Product> lista = productDao.getProductsByOrder(this.getKey());
                super.setProducts(lista);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         return super.getProducts();
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