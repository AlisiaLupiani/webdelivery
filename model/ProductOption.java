package model;

public interface ProductOption extends Consumation{
	
	public Integer getId();
	public void setId(Integer id);
	
	public String getName();
	public void setName(String name);
	
	public String getDescription();
	public void setDescription(String description);
	
	public Double getAddictionalPrice();
	public void setAddictionalPrice(Double price);
	
	public boolean isDefault();
	public void setDefault(boolean df);
	
	public ProductOptionGroup getProductOptionGroup();
	public void setProductOptionGroup(ProductOptionGroup productOptionGroup);
}
