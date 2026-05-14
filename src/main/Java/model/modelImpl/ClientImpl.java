package model.modelImpl;


import model.Client;


public class ClientImpl extends UserImpl implements Client {

    private String phone;
    private String address;

    public ClientImpl() {
        super();
        this.phone = "";
        this.address = "";
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }


}
