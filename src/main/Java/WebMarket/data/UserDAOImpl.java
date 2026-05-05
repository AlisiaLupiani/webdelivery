package WebMarket.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.User;
import model.modelImpl.UserImpl;



public class UserDAOImpl extends DAO implements UserDAO{

    public UserDAOImpl(DataLayer dataLayer) {
        super(dataLayer);
    }

    @Override
    public User getUserById(int id) throws DataException {
        User user = null;
        String query = "SELECT * FROM UTENTE WHERE id = ?";

        try (PreparedStatement preparedStatement = dataLayer.getConnection().prepareStatement(query)){
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    user = createUser(resultSet);
                }
            } 
        }catch (SQLException e){
            throw new DataException(e);
        }
        return user;

     }
            

    @Override
    public User getUserByEmail(String email) throws DataException {
        User user = null;
        String query = "SELECT * FROM UTENTE WHERE EMAIL = ?";

        try(PreparedStatement statement = dataLayer.getConnection().prepareStatement(query)) {
            statement.setString(1, email);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    user = createUser(resultSet);
                }      
            }
        }catch (SQLException e) {
                throw new DataException(e);
        }
        
       return user;
    }

    @Override
    public List<User> getAllUsers() throws DataException {
        List<User> result = null;
        String query = "SELECT * FROM UTENTE";

        try(PreparedStatement statement = dataLayer.getConnection().prepareStatement(query)){
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result.add(createUser(resultSet));

            }
        }catch(SQLException e){
            throw new DataException(e);
        }
        return null;
    }

    @Override
    public void addUser(User user) throws DataException {
        String query = "INSERT INTO UTENTE (NOME, COGNOME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?)";

        try(PreparedStatement statement = dataLayer.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());

            statement.executeUpdate();
        
        try(ResultSet resultSet = statement.getGeneratedKeys()){
            if(resultSet.next()){
                user.setId(resultSet.getInt(1));
            }
        }
        
    }catch(SQLException e){
            throw new DataException(e);
    }
}

    @Override
    public void updateUser(User user) throws DataException {
        String query = "UPDATE UTENTE SET NOME = ?, COGNOME = ?, EMAIL = ?, PASSWORD = ? WHERE ID = ?";

        try(PreparedStatement statement = dataLayer.getConnection().prepareStatement(query)){
            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setInt(5, user.getId());

            statement.executeUpdate();

            try(ResultSet resultSet = statement.getGeneratedKeys()){
                if(resultSet.next()){
                    user.setId(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataException(e);
        }

    }

    @Override
    public void deleteUser(int id) throws DataException {
        String query = "DELETE FROM UTENTE WHERE ID = ?";

        try(PreparedStatement statement = dataLayer.getConnection().prepareStatement(query)){
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataException(e);
        }
    }

    private User createUser(ResultSet rs) throws SQLException {
        User u = new UserImpl(); 
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setSurname(rs.getString("surname"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        return u;
    }

}
