package WebMarket.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.LogOrderState;
import model.Order;
import WebMarket.data.proxy.LogOrderStateProxy;

public class LogOrderStateDAOImpl extends DAO implements LogOrderStateDAO {

    private PreparedStatement sLogById;
    private PreparedStatement sLogByOrder;

    public LogOrderStateDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sLogById = getConnection().prepareStatement("SELECT * FROM log_order_state WHERE id=?");
            sLogByOrder = getConnection().prepareStatement("SELECT * FROM log_order_state WHERE order_id=?");
        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione LogOrderStateDAO", ex);
        }
    }

    protected LogOrderState createLogOrderState(ResultSet rs) throws SQLException {
        LogOrderStateProxy p = new LogOrderStateProxy(getDataLayer());
        p.setKey(rs.getInt("id"));
        p.setVersion(rs.getLong("version"));
        // Caricamento dei campi tramite proxy o setter
        return p;
    }

    @Override
    public LogOrderState getLogOrderStateById(int log_key) throws DataException {
        try {
            sLogById.setInt(1, log_key);
            try (ResultSet rs = sLogById.executeQuery()) {
                if (rs.next()) return createLogOrderState(rs);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getLogOrderStateById", ex);
        }
        return null;
    }

    @Override
    public List<LogOrderState> getLogOrderStateByOrder(Order order) throws DataException {
        List<LogOrderState> res = new ArrayList<>();
        try {
            sLogByOrder.setInt(1, order.getKey());
            try (ResultSet rs = sLogByOrder.executeQuery()) {
                while (rs.next()) {
                    res.add(createLogOrderState(rs)); // Qui l'errore "not applicable" sparirà
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getLogOrderStateByOrder", ex);
        }
        return res;
    }

    @Override
    public List<LogOrderState> getAllLogOrderStates() throws DataException {
        List<LogOrderState> res = new ArrayList<>();
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM log_order_state")) {
            while (rs.next()) res.add(createLogOrderState(rs));
        } catch (SQLException ex) {
            throw new DataException("Errore getAllLogOrderStates", ex);
        }
        return res;
    }

    @Override public void addLogOrderState(LogOrderState log) throws DataException {}
    @Override public void updateLogOrderState(LogOrderState log) throws DataException {}
    @Override public void deleteLogOrderState(LogOrderState log) throws DataException {}
}