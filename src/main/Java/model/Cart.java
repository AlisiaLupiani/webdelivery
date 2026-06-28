package model;

import java.util.List;

public interface Cart {

    int getKey();
    void setKey(int key);

    int getUserId();
    void setUserId(int userId);

    String getStato();
    void setStato(String stato);

    int getVersion();
    void setVersion(int version);

    List<CartItem> getElementi();
    void setElementi(List<CartItem> elementi);

    void addItem(CartItem item);
    void removeItem(int index);

    double getPrezzoTotaleCarrello();
}