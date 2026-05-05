package model.modelImpl;

import model.Food;

public class FoodImpl implements Food {

    private Integer id;
    private String name;

    // Costruttore vuoto richiesto per l'uso nei DAO
    public FoodImpl() {
        this.id = 0;
        this.name = "";
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}