package WebMarket.data.proxy;

import java.time.LocalDateTime;

import WebMarket.data.OrderDAO;
import WebMarket.data.UserDAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Order;
import model.OrderState;
import model.Staff;
import model.User;
import model.modelImpl.LogOrderStateImpl;



public class LogOrderStateProxy extends LogOrderStateImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    protected int idOrder;
    protected int idStaff;
    



    public LogOrderStateProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
    }

    
    public void setIdOrderNascosto(int id) {
        this.idOrder = id;
    }
    
    
    public void setIdStaffNascosto(int id) {
        this.idStaff = id;
    }
    
    @Override
    public void setId(Integer id) {
        super.setId(id);
        this.isDirty = true;
    }

    @Override
    public void setOrder(Order order) {
        super.setOrder(order);
        this.isDirty = true;
    }

    @Override
    public Order getOrder() {
        
        if (super.getOrder() == null && idOrder > 0) {
            try {
                OrderDAO orderDAO = (OrderDAO) dataLayer.getDAO(Order.class);
                
                super.setOrder(orderDAO.getOrderById(idOrder));
            } catch (DataException e) {
                e.printStackTrace();
            }
        }
        return super.getOrder();
    }

    @Override
    public void setStaff(Staff staff) {
        super.setStaff(staff);
        this.isDirty = true;
    }

    @Override
    public Staff getStaff() {
        if (super.getStaff() == null && idStaff > 0) {
            try {
                UserDAO staffDAO = (UserDAO) dataLayer.getDAO(User.class);

                User user = staffDAO.getUserById(idStaff);
                super.setStaff((Staff) user);
            }catch(DataException e){
                e.printStackTrace();
            }
        
            }
            return super.getStaff();
    }



    @Override
    public void setStateFrom(OrderState state) {
        super.setStateFrom(state);
        this.isDirty = true;
    }

    @Override
    public void setStateTo(OrderState state) {
        super.setStateTo(state);
        this.isDirty = true;
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
        super.setDateTime(dateTime);
        this.isDirty = true;
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