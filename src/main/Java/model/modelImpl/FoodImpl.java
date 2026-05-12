package model.modelImpl;

import framework.data.DataItemImpl;
import model.Food;

public class FoodImpl extends DataItemImpl<Integer> implements Food {

    protected Integer id;
    protected String name;


    public FoodImpl() {
        this.id = 0;
        this.name = "";
        
    }

    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    
}