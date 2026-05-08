package model;

import framework.data.DataItem;

public interface Food extends DataItem<Integer> {
    
    String getName();
    void setName(String name);

    // Metodi necessari per risolvere il conflitto nel file Impl
    Integer getId();
    void setId(Integer id);

    long getVersion();
    void setVersion(long version);
}