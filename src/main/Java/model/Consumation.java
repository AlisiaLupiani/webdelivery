package model;

import framework.data.DataItem;

// Deve estendere DataItem per avere i metodi della chiave (ID)
public interface Consumation extends DataItem<Integer> {
    String getName();
    void setName(String name);

    Double getPrice();
    void setPrice(Double price);
}