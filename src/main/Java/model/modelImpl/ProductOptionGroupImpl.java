package model.modelImpl;

import java.util.List;

import framework.data.DataItemImpl;
import model.ProductOption;
import model.ProductOptionGroup;

public class ProductOptionGroupImpl extends DataItemImpl<Integer> implements ProductOptionGroup {

    protected Integer key;
    protected String name;
    private List<ProductOption> options;


    public ProductOptionGroupImpl() {
        this.key = 0;
        this.name = "";
    
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
    
    @Override
public List<ProductOption> getOptions() {
    return this.options;
}

@Override
public void setOptions(List<ProductOption> options) {
    this.options = options;
}

   
}