package model.modelImpl;
import model.CartItem;
import model.ProductOption; // Sostituito Option con ProductOption
import model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartItemImpl implements CartItem {
    private Product prodotto;
    private List<ProductOption> opzioniScelte; // Sostituito qui
    private int quantita;

    public CartItemImpl() {
        this.opzioniScelte = new ArrayList<>();
    }

    @Override
    public Product getProdotto() { return prodotto; }

    @Override
    public void setProdotto(Product prodotto) { this.prodotto = prodotto; }

    @Override
    public List<ProductOption> getOpzioniScelte() { return opzioniScelte; }

    @Override
    public void setOpzioniScelte(List<ProductOption> opzioniScelte) { this.opzioniScelte = opzioniScelte; }

    @Override
    public void addOpzione(ProductOption opzione) { this.opzioniScelte.add(opzione); }

    @Override
    public int getQuantita() { return quantita; }

    @Override
    public void setQuantita(int quantita) { this.quantita = quantita; }

    @Override
    public double getPrezzoTotaleRiga() {
        double prezzoBase = (prodotto != null) ? prodotto.getPrice() : 0.0;
        double prezzoOpzioni = 0.0;
        
        for (ProductOption opzione : opzioniScelte) {
            // ATTENZIONE: Se qui ti dà ancora errore, controlla come si chiama 
            // il metodo getter dentro la tua classe ProductOption. 
            // Potrebbe chiamarsi getPrice() o in un altro modo!
            prezzoOpzioni += opzione.getAddictionalPrice(); 
        }
        
        return (prezzoBase + prezzoOpzioni) * quantita;
    }
}