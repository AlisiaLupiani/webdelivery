package WebMarket.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import framework.data.DataException;
import framework.data.DataLayer;
import model.User;



public class StaffDAOImpl extends UserDAOImpl {

    public StaffDAOImpl(DataLayer dataLayer) {
        super(dataLayer);
    }

    @Override
    public void init() throws DataException {
        super.init();
    }

    @Override
    public void destroy() throws DataException {
        super.destroy();
    }

    @Override
    public User addUser(User user) throws DataException {

        return super.addUser(user);

    }

    @Override
    public User updateUser(User user) throws DataException {

        return super.updateUser(user);

    }

    @Override
    protected User createUser(ResultSet rs) throws SQLException {

        return super.createUser(rs);

    }

    


}
