package model.modelImpl;

import java.util.ArrayList;
import java.util.List;

import model.Ingredient;
import model.Product;

public class ProductImpl implements Product {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private String image;
    private Integer preparationTime;
    private String procedure;
    private List<Ingredient> ingredients;

    // Costruttore vuoto per il DAO
    public ProductImpl() {
        this.id = 0;
        this.name = "";
        this.description = "";
        this.price = 0.0;
        this.image = "";
        this.preparationTime = 0;
        this.procedure = "";
        this.ingredients = new ArrayList<>();
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Double getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String getImage() {
        return this.image;
    }

    @Override
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public Integer getPreparationTime() {
        return this.preparationTime;
    }

    @Override
    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    @Override
    public String getProcedure() {
        return this.procedure;
    }

    @Override
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    @Override
    public List<Ingredient> getIngrediens() {
        return this.ingredients;
    }

    @Override
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}