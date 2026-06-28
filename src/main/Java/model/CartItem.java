package model;

import java.util.List;

public interface CartItem {

    int getKey();
    void setKey(int key);

    int getCartId();
    void setCartId(int cartId);

    int getProductId();
    void setProductId(int productId);

    Product getProdotto();
    void setProdotto(Product prodotto);

    List<ProductOption> getOpzioniScelte();
    void setOpzioniScelte(List<ProductOption> opzioniScelte);
    void addOpzione(ProductOption opzione);

    int getQuantita();
    void setQuantita(int quantita);

    double getPrezzoUnitario();
    void setPrezzoUnitario(double prezzoUnitario);

    int getVersion();
    void setVersion(int version);

    double getPrezzoTotaleRiga();
}