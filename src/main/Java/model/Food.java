package model;

import framework.data.DataItem;

public interface Food extends DataItem<Integer> {
    
    String getName();
    void setName(String name);

    
    Integer getId();
    void setId(Integer id);

    
}