package model.modelImpl;

import model.Product;
import model.Ingredient;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class ProductImpl implements Product {

    protected Integer key;
    protected String name;
    protected String description;
    protected Double price;
    protected String image;
    protected Integer preparationTime;
    protected String procedure;
    protected List<Ingredient> ingredients;
    protected long version;
    protected String category;

    public ProductImpl() {
        this.key = 0;
        this.name = "";
        this.description = "";
        this.price = 0.0;
        this.image = "";
        this.preparationTime = 0;
        this.procedure = "";
        this.ingredients = new ArrayList<>();
        this.version = 0;
        this.category = "Altro";
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
    public String getName() { return name; }
    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getDescription() { return description; }
    @Override
    public void setDescription(String description) { this.description = description; }

    @Override
    public Double getPrice() { return price; }
    @Override
    public void setPrice(Double price) { this.price = price; }

    @Override
    public String getImage() { return image; }
    @Override
    public void setImage(String image) { this.image = image; }

    @Override
    public Integer getPreparationTime() { return preparationTime; }
    @Override
    public void setPreparationTime(Integer preparationTime) { this.preparationTime = preparationTime; }

    @Override
    public String getProcedure() { return procedure; }
    @Override
    public void setProcedure(String procedure) { this.procedure = procedure; }

    @Override
    public List<Ingredient> getIngredients() { return ingredients; }
    @Override
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    @Override
    public long getVersion() { return version; }
    @Override
    public void setVersion(long version) { this.version = version; }

    @Override
public String getCategory() { return category; }

@Override
public void setCategory(String category) { this.category = category; }
}