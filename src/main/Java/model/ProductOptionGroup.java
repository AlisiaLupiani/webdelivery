package model;

import framework.data.DataItem;


public interface ProductOptionGroup extends DataItem<Integer> {
    
    public Integer getId();
    public void setId(Integer id);
    
    public String getName();
    public void setName(String name);

}