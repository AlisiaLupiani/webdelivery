package model;

import java.util.List;
import java.util.Map;

import framework.data.DataItem;


public interface Product extends Consumation, DataItem<Integer> {
    
    public Integer getId();
    public void setId(Integer id);
    
    public String getName();
    public void setName(String name);

    public String getDescription();
    public void setDescription(String description);

    public Double getPrice();
    public void setPrice(Double price);

    public String getImage();
    public void setImage(String image);

    public Integer getPreparationTime();
    public void setPreparationTime(Integer preparationTime);

    public String getProcedure();
    public void setProcedure(String procedure);
    
    public List<Ingredient> getIngredients();
    public void setIngredients(List<Ingredient> ingredients);
    
    public String getCategory();
    public void setCategory(String category);
    
}