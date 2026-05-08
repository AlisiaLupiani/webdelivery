package model.modelImpl;

import model.ProductOptionGroup;

public class ProductOptionGroupImpl implements ProductOptionGroup {

    protected Integer key;
    protected String name;
    protected long version;

    public ProductOptionGroupImpl() {
        this.key = 0;
        this.name = "";
        this.version = 0;
    }

    // Risolve gli errori su getKey e setKey
    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public void setKey(Integer key) {
        this.key = key;
    }

    // Metodi ID (Alias della Key)
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

    // Risolve gli errori su getVersion e setVersion
    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }
}