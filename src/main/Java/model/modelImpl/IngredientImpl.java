package model.modelImpl;

import framework.data.DataItemImpl;
import model.Ingredient;


public class IngredientImpl extends DataItemImpl<Integer> implements Ingredient {

    protected Integer key;
    protected String quantity;
    protected String name;
    

    public IngredientImpl() {
        this.name = "";
        this.key = 0;
        this.quantity = "";
        
    }

    @Override
    public Integer getKey() { return key; }
    @Override
    public void setKey(Integer key) { this.key = key; }

    @Override
    public Integer getId() { return key; }
    @Override
    public void setId(Integer id) { this.key = id; }

    @Override
    public String getQuantity() { return quantity; }
    @Override
    public void setQuantity(String quantity) { this.quantity = quantity; }

    @Override
    public String getName() { return name; }
    @Override   
    public void setName(String name) { this.name = name; }

   
}
