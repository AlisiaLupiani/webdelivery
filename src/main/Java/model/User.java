package model;

import framework.data.DataItem;

public interface User extends DataItem<Integer> {

    public Integer getId();
    public void setId(Integer id);

    public String getName();
    public void setName(String name);

    public String getSurname();
    public void setSurname(String surname);

    public String getEmail();
    public void setEmail(String email);

    public String getPassword();
    public void setPassword(String password);

}