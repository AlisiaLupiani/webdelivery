package WebMarket.data.proxy;

import java.time.LocalDateTime;

import WebMarket.data.OrderDAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Order;
import model.OrderState;
import model.Staff;
import model.modelImpl.LogOrderStateImpl;


public class LogOrderStateProxy extends LogOrderStateImpl {

    protected DataLayer dataLayer;
    protected boolean isDirty;

    protected int idOrderNascosto;
    protected int idStaffNascosto;
    



    public LogOrderStateProxy(DataLayer dl) {
        super();
        this.dataLayer = dl;
        this.isDirty = false;
    }

    
    public void setIdOrderNascosto(int id) {
        this.idOrderNascosto = id;
    }
    
    
    public void setIdStaffNascosto(int id) {
        this.idStaffNascosto = id;
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
        
        if (super.getOrder() == null && idOrderNascosto > 0) {
            try {
                OrderDAO orderDAO = (OrderDAO) dataLayer.getDAO(Order.class);
                
                super.setOrder(orderDAO.getOrderById(idOrderNascosto));
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
        if (super.getStaff() == null && idStaffNascosto > 0) {
            try {
                StaffDAO staffDAO = (StaffDAO) dataLayer.getDAO(Staff.class);

                super.setStaff(staffDAO.getStaffById(idStaffNascosto));
            }catch{
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