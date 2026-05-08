package model;

import framework.data.DataItem;

// Estendiamo DataItem per abilitare getKey() e setKey()
public interface ProductOptionGroup extends DataItem<Integer> {
    
    public Integer getId();
    public void setId(Integer id);
    
    public String getName();
    public void setName(String name);

    // Metodi tecnici necessari per il framework
    public long getVersion();
    public void setVersion(long version);
}