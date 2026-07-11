package WebMarket.data.proxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import model.Client;
import model.OrderState;
import model.PaymentMethod;
import model.Product;
import model.User;
import model.modelImpl.OrderImpl;


public class OrderProxy extends OrderImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;
    protected Integer idUtente;


    public OrderProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
        this.idUtente = 0;
        super.setProducts(null);
    }

    public void setIdUtente(Integer id) {
        this.idUtente = id;
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
    public Client getClient() {
        if (super.getClient() == null && idUtente != null && idUtente > 0) {
            try {
                UserDAO userDAO = (UserDAO) dataLayer.getDAO(User.class);
                User user = userDAO.getUserById(idUtente);
                if (user instanceof Client) {
                    super.setClient((Client) user);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return super.getClient();
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

                List<Product> lista = productDao.getProductsByOrder(this);
                super.setProducts(lista);
                
            } catch (Exception e) {
                return null;
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
