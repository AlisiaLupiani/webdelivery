package model.modelImpl;

import model.Consumation;

public class ConsumationImpl implements Consumation {

    protected Integer key;
    protected String name;
    protected Double price;
    protected long version;

    public ConsumationImpl() {
        this.key = 0;
        this.name = "";
        this.price = 0.0;
        this.version = 0;
    }

    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public void setKey(Integer key) {
        this.key = key;
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

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }
}