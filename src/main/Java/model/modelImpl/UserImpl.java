package model.modelImpl;

import model.User;

public class UserImpl implements User {

    protected Integer key;
    protected String name;
    protected String surname;
    protected String email;
    protected String password;
    protected long version;

    public UserImpl() {
        this.key = 0;
        this.name = "";
        this.surname = "";
        this.email = "";
        this.password = "";
        this.version = 0;
    }

    @Override
    public Integer getKey() { return key; }
    @Override
    public void setKey(Integer key) { this.key = key; }

    @Override
    public Integer getId() { return key; }
    @Override
    public void setId(Integer id) { this.key = id; }

    @Override
    public String getName() { return name; }
    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getSurname() { return surname; }
    @Override
    public void setSurname(String surname) { this.surname = surname; }

    @Override
    public String getEmail() { return email; }
    @Override
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getPassword() { return password; }
    @Override
    public void setPassword(String password) { this.password = password; }

    @Override
    public long getVersion() { return version; }
    @Override
    public void setVersion(long version) { this.version = version; }
}