package WebMarket.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import WebMarket.data.proxy.UserProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.User;




public class UserDAOImpl extends DAO implements UserDAO{

    private PreparedStatement sUserById;
    private PreparedStatement sUserByEmail;
    private PreparedStatement sAllUsers;
    private PreparedStatement sAddUser;
    private PreparedStatement sUpdateUser;
    private PreparedStatement sDeleteUser;
    private static final String TABLE = "UTENTE";



    public UserDAOImpl(DataLayer dataLayer) {
        super(dataLayer);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sUserById= connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
            sUserByEmail = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE EMAIL = ?");
            sAllUsers = connection.prepareStatement("SELECT * FROM " + TABLE);

            sAddUser = connection.prepareStatement(
                "INSERT INTO" + TABLE + "(NOME, COGNOME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            sUpdateUser = connection.prepareStatement(
                "UPDATE" + TABLE + "NOME = ?, COGNOME = ?, EMAIL = ?, PASSWORD = ? WHERE ID = ?");

            sDeleteUser = connection.prepareStatement(
                "DELETE FROM" + TABLE + "WHERE ID = ?");


        } catch (SQLException e) {
            throw new DataException("Error initializing webdelivery data layer", e);

        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if(sUserById != null) sUserById.close();
            if(sUserByEmail != null) sUserByEmail.close();
            if(sAllUsers != null) sAllUsers.close();

            if(sAddUser != null) sAddUser.close();
            if(sUpdateUser != null) sUpdateUser.close();
            if(sDeleteUser != null) sDeleteUser.close();

            super.destroy();
            
        } catch (SQLException e) {
            throw new DataException("Error closing webdelivery data layer", e);
        }
    }



    @Override
    public User getUserById(int id) throws DataException {
        User user = null;
        if(dataLayer.getCache().has(User.class, id)){
            user  = dataLayer.getCache().get(User.class, id);
        }
        else{
            try {
                sUserById.setInt(1, id);
                try (ResultSet resultSet = sUserById.executeQuery()){
                    if (resultSet.next()){
                        user = createUser(resultSet);
                        dataLayer.getCache().add(User.class, user);
                    }
                }

            } catch (SQLException e) {
                throw new DataException("Unable to find the requested item", e);

            }
        }
        return user;

     }
            

    @Override
    public User getUserByEmail(String email) throws DataException {
        User user = null;
        if(dataLayer.getCache().has(User.class, email)){
            user = dataLayer.getCache().get(User.class, email);
        }
        else{
            try {
                sUserByEmail.setString(1, email);
                try (ResultSet resultSet = sUserByEmail.executeQuery()){
                    if(resultSet.next()){
                        user = createUser(resultSet);
                        dataLayer.getCache().add(User.class, user);
                    }
                }
            }catch(SQLException e){
                throw new DataException("Unable to find the requested item", e);
            } 

        }
       return user;
    }


    @Override
    public List<User> getAllUsers() throws DataException {
        List<User> result = null;
        
        try(ResultSet resultSet = sAllUsers.executeQuery()){

            while(resultSet.next()){
                User user = null;

                Integer id = resultSet.getInt("ID");

                if(dataLayer.getCache().has(User.class, id)){
                    user = dataLayer.getCache().get(User.class, id);
                }
                else{
                    user = createUser(resultSet);
                    dataLayer.getCache().add(User.class, user);
                }
                result.add(user);

            }
        }catch(SQLException e){
                throw new DataException("Unable to find the requested item", e);

            }        
        
        return null;
    }

    @Override
    public void addUser(User user) throws DataException {

        try {
            sAddUser.setString(1, user.getName());
            sAddUser.setString(2, user.getSurname());
            sAddUser.setString(3, user.getEmail());
            sAddUser.setString(4, user.getPassword());

            long initialVersion = 1;
            sAddUser.setLong(5, initialVersion);

            if(sAddUser.executeUpdate() == 1){
                try(ResultSet resultSet = sAddUser.getGeneratedKeys()){
                    if(resultSet.next()){
                        Integer newKey = resultSet.getInt(1);
                        user.setKey(newKey);

                        user.setVersion(initialVersion);
                    }
                }
                dataLayer.getCache().add(User.class, user);
            }
   
        } catch (SQLException e) {
                throw new DataException("Unable to add user to the database", e);
        }
        

    }

    @Override
    public void updateUser(User user) throws DataException {
        try {
            sUpdateUser.setString(1, user.getName());
            sUpdateUser.setString(2, user.getSurname());
            sUpdateUser.setString(3, user.getEmail());
            sUpdateUser.setString(4, user.getPassword());

            long currentVersion = user.getVersion();
            long nextVersion = currentVersion + 1;
            sUpdateUser.setLong(5, nextVersion);

            sUpdateUser.setInt(6, user.getKey());
            sUpdateUser.setLong(7, currentVersion);

            int affectedRows = sUpdateUser.executeUpdate();

            if(affectedRows == 0){

                throw new DataException("l'utente è stato modificato da un altro processo");
            }
            else{
                user.setVersion(nextVersion);

                if(user instanceof UserProxy){
                    ((UserProxy) user).setClean();
                }
            }    
        } catch (SQLException e) {
            throw new DataException("Unable to update user in the database", e);
        }

    }

    @Override
    public void deleteUser(User user) throws DataException {
        try {
            sDeleteUser.setInt(1, user.getKey());
            
            int affectedRows = sDeleteUser.executeUpdate();

            if(affectedRows > 0){
                dataLayer.getCache().delete(User.class, user.getKey());
            }
            
        } catch (SQLException e) {
            throw new DataException("Unable to delete user", e);
        }
    }

    protected User createUser(ResultSet rs) throws SQLException {
        UserProxy user = new UserProxy(getDataLayer()); 

        user.setKey(rs.getInt("ID")); 
    
    
        user.setVersion(rs.getLong("VERSION")); 

        user.setName(rs.getString("NAME"));
        user.setSurname(rs.getString("SURNAME"));
        user.setEmail(rs.getString("EMAIL"));
        user.setPassword(rs.getString("PASSWORD"));

        user.setClean();

        return user;
    }

}
