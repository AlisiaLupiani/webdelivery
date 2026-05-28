package model.modelImpl;
import model.Cart;
import model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartImpl implements Cart {
    private List<CartItem> elementi;

    public CartImpl() {
        this.elementi = new ArrayList<>();
    }

    @Override
    public List<CartItem> getElementi() { return elementi; }

    @Override
    public void setElementi(List<CartItem> elementi) { this.elementi = elementi; }

    @Override
    public void addItem(CartItem item) { this.elementi.add(item); }

    @Override
    public void removeItem(int index) {
        if (index >= 0 && index < elementi.size()) {
            this.elementi.remove(index);
        }
    }

    // La logica di calcolo totale
    @Override
    public double getPrezzoTotaleCarrello() {
        double totale = 0.0;
        for (CartItem item : elementi) {
            totale += item.getPrezzoTotaleRiga();
        }
        return totale;
    }
}