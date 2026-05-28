package model;

import java.util.List;

public interface CartItem {
    Product getProdotto();
    void setProdotto(Product prodotto);
    
    // Sostituito Option con ProductOption
    List<ProductOption> getOpzioniScelte();
    void setOpzioniScelte(List<ProductOption> opzioniScelte);
    void addOpzione(ProductOption opzione);
    
    int getQuantita();
    void setQuantita(int quantita);
    
    double getPrezzoTotaleRiga();
}