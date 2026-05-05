package model.modelImpl;

import model.Consumation;

public class ConsumationImpl implements Consumation {

    private int id;
    private String nome;
    private String descrizione;
    private Double prezzo;

    // Costruttore vuoto (necessario per i DAO)
    public ConsumationImpl() {
        this.id = 0;
        this.nome = "";
        this.descrizione = "";
        this.prezzo = 0.0;
    }

    // Implementazione del metodo richiesto dall'interfaccia
    @Override
    public Double getPrice() {
        return this.prezzo;
    }

    // Getter e Setter per gli altri campi (necessari per il DAO e la logica)
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }
}