package model;

import java.util.List;

public interface Cart {
    List<CartItem> getElementi();
    void setElementi(List<CartItem> elementi);
    
    void addItem(CartItem item);
    void removeItem(int index);
    
    double getPrezzoTotaleCarrello();
}