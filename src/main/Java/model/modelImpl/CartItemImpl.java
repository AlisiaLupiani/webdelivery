package model.modelImpl;

import model.CartItem;
import model.Product;
import model.ProductOption;

import java.util.ArrayList;
import java.util.List;

public class CartItemImpl implements CartItem {

    private int key;
    private int cartId;
    private int productId;
    private Product prodotto;
    private List<ProductOption> opzioniScelte;
    private int quantita;
    private double prezzoUnitario;
    private int version;

    public CartItemImpl() {
        this.opzioniScelte = new ArrayList<>();
        this.quantita = 1;
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
    public int getCartId() {
        return cartId;
    }

    @Override
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    @Override
    public int getProductId() {
        return productId;
    }

    @Override
    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public Product getProdotto() {
        return prodotto;
    }

    @Override
    public void setProdotto(Product prodotto) {
        this.prodotto = prodotto;

        if (prodotto != null) {
            this.productId = prodotto.getKey();
        }
    }

    @Override
    public List<ProductOption> getOpzioniScelte() {
        return opzioniScelte;
    }

    @Override
    public void setOpzioniScelte(List<ProductOption> opzioniScelte) {
        this.opzioniScelte = opzioniScelte;
    }

    @Override
    public void addOpzione(ProductOption opzione) {
        this.opzioniScelte.add(opzione);
    }

    @Override
    public int getQuantita() {
        return quantita;
    }

    @Override
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    @Override
    public double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    @Override
    public void setPrezzoUnitario(double prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
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
    public double getPrezzoTotaleRiga() {
        return prezzoUnitario * quantita;
    }
}