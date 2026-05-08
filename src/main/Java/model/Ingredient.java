package model;

import framework.data.DataItem;

public interface Ingredient extends DataItem<Integer> {

    String getQuantity();
    void setQuantity(String quantity);

    Food getFood();
    void setFood(Food food);

    // Metodi per la compatibilità con l'implementazione
    Integer getId();
    void setId(Integer id);

    long getVersion();
    void setVersion(long version);
}