package model;

public interface Ingredient {
	
	public Integer getId();
	public void setId(Integer id);
	
	public Food getFood();
	public void setFood(Food food);
	
	public String getQuantity();
	public void setQuantity(String quantity);
}

