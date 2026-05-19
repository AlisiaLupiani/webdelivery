package model;

import framework.data.DataItem;

public interface Ingredient extends DataItem<Integer> {



    String getName();
    void setName(String name);

    String getQuantity();
    void setQuantity(String quantity);

    Food getFood();
    void setFood(Food food);

    Integer getId();
    void setId(Integer id);

}