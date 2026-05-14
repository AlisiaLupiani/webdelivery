package WebMarket.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import framework.data.DataException;
import framework.data.DataLayer;




public class ClientDAOImpl extends UserDAOImpl {

    protected PreparedStatement sAddClient;

    public ClientDAOImpl(DataLayer dataLayer) {
        super(dataLayer);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sAddUser = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + " (NOME, COGNOME, EMAIL, PASSWORD, VERSION) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);


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


}
