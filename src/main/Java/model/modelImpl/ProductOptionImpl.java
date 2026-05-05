package model.modelImpl;

import model.Consumation;
import model.ProductOption;
import model.ProductOptionGroup;

public class ProductOptionImpl implements ProductOption {

	private Integer id;
	private String name;
	private String description;
	private Double price;
	private boolean defaultOption;
	private ProductOptionGroup productOptionGroup;
	private Consumation consumation;


	public ProductOptionImpl(){
		
	}

	@Override
	public Integer getId() {

		return null;
	}

	@Override
	public void setId(Integer id) {

	}

	@Override
	public String getName() {

		return null;
	}

	@Override
	public void setName(String name) {

	}

	@Override
	public String getDescription() {

		return null;
	}

	@Override
	public void setDescription(String description) {

	}

	@Override
	public Double getPrice() {

		return this.consumation.getPrice() + getAddictionalPrice();
	}

	@Override
	public Double getAddictionalPrice() {

		return null;
	}

	@Override
	public void setAddictionalPrice(Double price) {

	}

	@Override
	public boolean isDefault() {

		return false;
	}

	@Override
	public void setDefault(boolean df) {

	}

	@Override
	public ProductOptionGroup getProductOptionGroup() {

		return null;
	}

	@Override
	public void setProductOptionGroup(ProductOptionGroup productOptionGroup) {
	
	}

}
