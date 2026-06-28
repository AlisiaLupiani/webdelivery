package model.modelImpl;

import model.Cart;
import model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartImpl implements Cart {

    private int key;
    private int userId;
    private String stato;
    private int version;
    private List<CartItem> elementi;

    public CartImpl() {
        this.elementi = new ArrayList<>();
        this.stato = "ATTIVO";
        this.version = 1;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String getStato() {
        return stato;
    }

    @Override
    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public List<CartItem> getElementi() {
        return elementi;
    }

    @Override
    public void setElementi(List<CartItem> elementi) {
        this.elementi = elementi;
    }

    @Override
    public void addItem(CartItem item) {
        this.elementi.add(item);
    }

    @Override
    public void removeItem(int index) {
        if (index >= 0 && index < elementi.size()) {
            this.elementi.remove(index);
        }
    }

    @Override
    public double getPrezzoTotaleCarrello() {
        double totale = 0.0;

        for (CartItem item : elementi) {
            totale += item.getPrezzoTotaleRiga();
        }

        return totale;
    }
}