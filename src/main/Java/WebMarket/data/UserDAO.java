package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.User;


public interface UserDAO {

    User getUserById(int id) throws DataException;
    
    User getUserByEmail(String name) throws DataException;

    List<User> getAllUsers() throws DataException;
    
    void addUser(User user) throws DataException;
    
    void updateUser(User user) throws DataException;
    
    void deleteUser(int id) throws DataException;
    


    
}
