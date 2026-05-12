package model.modelImpl;

import framework.data.DataItemImpl;
import model.Consumation;

public class ConsumationImpl extends DataItemImpl<Integer> implements Consumation {

    protected String name;
    protected Double price;


    public ConsumationImpl() {
        
        this.name = "";
        this.price = 0.0;
        
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Double getPrice() {
        return price;
    }

    @Override
    public void setPrice(Double price) {
        this.price = price;
    }

}