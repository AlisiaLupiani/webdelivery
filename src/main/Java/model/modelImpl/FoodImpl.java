package model.modelImpl;

import model.Food;

public class FoodImpl implements Food {

    protected Integer key;
    protected String name;
    protected long version;

    public FoodImpl() {
        this.key = 0;
        this.name = "";
        this.version = 0;
    }

    // Metodi per la chiave (richiesti dal framework DataItem)
    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public void setKey(Integer key) {
        this.key = key;
    }

    // Alias per ID (richiesti dall'interfaccia Food)
    @Override
    public Integer getId() {
        return key;
    }

    @Override
    public void setId(Integer id) {
        this.key = id;
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
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }
}