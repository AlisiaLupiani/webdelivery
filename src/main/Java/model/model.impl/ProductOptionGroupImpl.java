package model.impl;

import model.ProductOptionGroup;

public class ProductOptionGroupImpl implements ProductOptionGroup {

    private Integer id;
    private String name;

    // Costruttore vuoto per il DAO
    public ProductOptionGroupImpl() {
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