package model;

import java.util.List;

public interface Product extends Consumation{
	
	
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
	
	public List<Ingredient> getIngrediens();
	public void setIngredients(List<Ingredient> ingredients);
}

