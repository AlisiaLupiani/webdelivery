package model;

import java.util.List;
import framework.data.DataItem;

// Assicuriamoci che estenda Consumation
public interface Product extends Consumation {
    
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
    
    // Corretto il nome da getIngrediens a getIngredients
    public List<Ingredient> getIngredients();
    public void setIngredients(List<Ingredient> ingredients);

    // Metodi necessari per il framework
    public long getVersion();
    public void setVersion(long version);
}