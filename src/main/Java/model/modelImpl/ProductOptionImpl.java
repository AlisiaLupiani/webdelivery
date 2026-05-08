package model.modelImpl;

import model.ProductOption;
import model.ProductOptionGroup;

public class ProductOptionImpl implements ProductOption {

    protected Integer key;
    protected String name;
    protected String description;
    protected Double addictionalPrice;
    protected boolean isDefault;
    protected ProductOptionGroup productOptionGroup;
    protected long version;

    public ProductOptionImpl() {
        this.key = 0;
        this.name = "";
        this.description = "";
        this.addictionalPrice = 0.0;
        this.isDefault = false;
        this.productOptionGroup = null;
        this.version = 0;
    }

    // Metodi per la Chiave Primaria (DataItem)
    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public void setKey(Integer key) {
        this.key = key;
    }

    // Metodi per l'ID (Alias di Key)
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
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Double getAddictionalPrice() {
        return addictionalPrice;
    }

    @Override
    public void setAddictionalPrice(Double price) {
        this.addictionalPrice = price;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setDefault(boolean df) {
        this.isDefault = df;
    }

    @Override
    public ProductOptionGroup getProductOptionGroup() {
        return productOptionGroup;
    }

    @Override
    public void setProductOptionGroup(ProductOptionGroup productOptionGroup) {
        this.productOptionGroup = productOptionGroup;
    }

    // Metodo richiesto da Consumation (ereditato)
    @Override
    public Double getPrice() {
        return addictionalPrice;
    }

    @Override
    public void setPrice(Double price) {
        this.addictionalPrice = price;
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