package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.UserDAO;
import WebMarket.data.proxy.ClientProxy;
import WebMarket.data.proxy.ProprietorProxy;
import WebMarket.data.proxy.StaffProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Client;
import model.Proprietor;
import model.Staff;
import model.User;







public class UserDAOImpl extends DAO implements UserDAO {

    protected PreparedStatement sUserById;
    protected PreparedStatement sUserByEmail;
    protected PreparedStatement sAllUsers;
    protected PreparedStatement sAddUser;
    protected PreparedStatement sUpdateUser;
    protected PreparedStatement sDeleteUser;
    protected static final String TABLE = "UTENTE";

    public UserDAOImpl(DataLayer dataLayer) {
        super(dataLayer);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sUserById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
            sUserByEmail = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE EMAIL = ?");
            sAllUsers = getConnection().prepareStatement("SELECT * FROM " + TABLE);

            sAddUser = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + "(NOME, COGNOME, EMAIL, PASSWORD, RUOLO, INDIRIZZO, TELEFONO, VERSION) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

            sUpdateUser = getConnection().prepareStatement(
                "UPDATE " + TABLE + " SET NOME = ?, COGNOME = ?, EMAIL = ?, PASSWORD = ?, RUOLO = ?, INDIRIZZO = ?, TELEFONO = ?, VERSION = ? WHERE ID = ? AND VERSION = ?");

            sDeleteUser = getConnection().prepareStatement(
                "DELETE FROM " + TABLE + " WHERE ID = ?");

        } catch (SQLException e) {
            throw new DataException("Error initializing WebMarket data layer", e);
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
            throw new DataException("Error closing WebMarket data layer", e);
        }
    }

    @Override
    public User getUserById(int id) throws DataException {
        User user = null;
        if (getDataLayer().getCache().has(User.class, id)) {
            user = getDataLayer().getCache().get(User.class, id);
        } else {
            try {
                sUserById.setInt(1, id);
                try (ResultSet resultSet = sUserById.executeQuery()) {
                    if (resultSet.next()) {
                        user = createUser(resultSet);
                        getDataLayer().getCache().add(User.class, user);
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Unable to find user by ID", e);
            }
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) throws DataException {
        User user = null;
        if (getDataLayer().getCache().has(User.class, email)) {
            user = getDataLayer().getCache().get(User.class, email);
        } else {
            try {
                sUserByEmail.setString(1, email);
                try (ResultSet resultSet = sUserByEmail.executeQuery()) {
                    if (resultSet.next()) {
                        user = createUser(resultSet);
                        getDataLayer().getCache().add(User.class, user);
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Unable to find user by email", e);
            }
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() throws DataException {
        List<User> result = new ArrayList<>();
        try (ResultSet resultSet = sAllUsers.executeQuery()) {
            while (resultSet.next()) {
                User user;
                Integer id = resultSet.getInt("ID");
                if (getDataLayer().getCache().has(User.class, id)) {
                    user = getDataLayer().getCache().get(User.class, id);
                } else {
                    user = createUser(resultSet);
                    getDataLayer().getCache().add(User.class, user);
                }
                result.add(user);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to retrieve all users", e);
        }
        return result;
    }

    @Override
    public User addUser(User user) throws DataException {
        try {
            sAddUser.setString(1, user.getName());
            sAddUser.setString(2, user.getSurname());
            sAddUser.setString(3, user.getEmail());
            sAddUser.setString(4, user.getPassword());
            
            // Inserisco il ruolo
            if(user instanceof Client) sAddUser.setString(5, "CLIENTE");
            else if(user instanceof Staff) sAddUser.setString(5, "STAFF");
            else if(user instanceof Proprietor) sAddUser.setString(5, "ADMIN");
            else sAddUser.setString(5, "CLIENTE");

            // Se è un cliente, gestisco i campi aggiuntivi
            if(!(user instanceof Client)){
                sAddUser.setNull(6, java.sql.Types.VARCHAR);
                sAddUser.setNull(7, java.sql.Types.VARCHAR);
            }else{

                sAddUser.setString(6, ((Client)user).getAddress());
                sAddUser.setString(7, ((Client)user).getPhone());
            }
            
            // Gestisco la versone
            long initialVersion = 1;
            sAddUser.setLong(5, initialVersion);

            if (sAddUser.executeUpdate() == 1) {
                try (ResultSet resultSet = sAddUser.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        Integer newKey = resultSet.getInt(1);
                        user.setKey(newKey);
                        user.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(User.class, user);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to add user", e);
        }
        return user;
    }

    @Override
    public User updateUser(User user) throws DataException {
        try {
            sUpdateUser.setString(1, user.getName());
            sUpdateUser.setString(2, user.getSurname());
            sUpdateUser.setString(3, user.getEmail());
            sUpdateUser.setString(4, user.getPassword());

             // Modifico il ruolo
            if(user instanceof Client) sAddUser.setString(5, "CLIENTE");
            else if(user instanceof Staff) sAddUser.setString(5, "STAFF");
            else if(user instanceof Proprietor) sAddUser.setString(5, "ADMIN");
            else sAddUser.setString(5, "CLIENTE");

            // Se è un cliente, gestisco i campi aggiuntivi
            if(!(user instanceof Client)){
                sAddUser.setNull(6, java.sql.Types.VARCHAR);
                sAddUser.setNull(7, java.sql.Types.VARCHAR);
            }else{
                sAddUser.setString(6, ((Client)user).getAddress());
                sAddUser.setString(7, ((Client)user).getPhone());
            }

            // Gestisco la versione
            long currentVersion = user.getVersion();
            long nextVersion = currentVersion + 1;
            sUpdateUser.setLong(8, nextVersion);

            // Gestisco l'optimistical lock
            sUpdateUser.setInt(9, user.getKey());
            sUpdateUser.setLong(10, currentVersion);

            if (sUpdateUser.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed: user modified by another process");
            } else {
                user.setVersion(nextVersion);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to update user", e);
        }
        return user;
    }

    @Override
    public void deleteUser(User user) throws DataException {
        try {
            sDeleteUser.setInt(1, user.getKey());
            if (sDeleteUser.executeUpdate() > 0) {
                getDataLayer().getCache().delete(User.class, user.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Unable to delete user", e);
        }
    }

    protected User createUser(ResultSet rs) throws SQLException {
        User user = null;
        
        // Istanzio l'oggetto in base al ruolo
        String ruolo = rs.getString("RUOLO"); 
        
        if(ruolo.equals("STAFF")) user = new StaffProxy(dataLayer);
        else if(ruolo.equals("ADMIN")) user = new ProprietorProxy(dataLayer);
        else user = new ClientProxy(dataLayer);

        user.setKey(rs.getInt("ID"));
        user.setVersion(rs.getLong("VERSION"));
        user.setName(rs.getString("NOME"));
        user.setSurname(rs.getString("COGNOME"));
        user.setEmail(rs.getString("EMAIL"));
        user.setPassword(rs.getString("PASSWORD"));

        if(ruolo == "CLIENTE"){
            ((Client)user).setAddress(rs.getString("INDIRIZZO"));
            ((Client)user).setPhone(rs.getString("TELEFONO"));
        }


        return user;
    }
}