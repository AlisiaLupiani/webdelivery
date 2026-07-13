package model;

import framework.data.DataItem;

public interface ProductOptionGroup extends DataItem<Integer> {

    Integer getId();
    void setId(Integer id);

    String getName();
    void setName(String name);

    boolean isSingleChoice();
    void setSingleChoice(boolean singleChoice);

    java.util.List<ProductOption> getOptions();
    void setOptions(java.util.List<ProductOption> options);
}
