package model.modelImpl;

import model.ProductOption;
import model.ProductOptionGroup;


public class ProductOptionImpl implements ProductOption{
	
	Integer id;
	Double price;
	String name;
	Double addictionalPrice;
	String description;
	ProductOptionGroup productOptionGroup;
	boolean defaultOption;

	@Override
	public Double getPrice(){
		return this.price;
	}

	@Override
	public void setPrice(double price){
		this.price = price;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
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
		return this.addictionalPrice;
	}

	@Override
	public void setAddictionalPrice(Double price) {
		this.addictionalPrice = addictionalPrice + price;
	}
	
	@Override
	public ProductOptionGroup getProductOptionGroup() {
		return productOptionGroup;
	}

	@Override
	public void setProductOptionGroup(ProductOptionGroup productOptionGroup) {
		this.productOptionGroup = productOptionGroup;
	}

	@Override
	public boolean isDefault(){
		return defaultOption;
	}

	@Override
	public void setDefault(boolean df){
		this.defaultOption = df;
	}


}
