package model.modelImpl;

import model.Consumation;

public class ConsumationImpl implements Consumation {

    private Double price;

    public ConsumationImpl() {
        super();
        this.price = 0.0;
    }


	public Double getPrice(){
        return this.price;
    };
	public void setPrice(){
        this.price = price;
    };


    
}