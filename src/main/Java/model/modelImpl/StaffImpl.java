package model.modelImpl;

import model.Staff;

public class StaffImpl extends UserImpl implements Staff {

    private boolean disposability;
    private String vehicle;

    // Costruttore vuoto per il DAO
    public StaffImpl() {
        super(); // Chiama il costruttore di UserImpl per inizializzare i campi base
        this.disposability = false;
        this.vehicle = "";
    }

    @Override
    public boolean getDisposabily() {
        return this.disposability;
    }

    @Override
    public void setDisposability(boolean disposability) {
        this.disposability = disposability;
    }

    @Override
    public String getVehicle() {
        return this.vehicle;
    }

    @Override
    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }
}