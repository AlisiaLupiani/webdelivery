package WebMarket.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.proxy.LogOrderStateProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.LogOrderState;
import model.Order;

public class LogOrderStateDAOImpl extends DAO implements LogOrderStateDAO {

    private PreparedStatement sLogById;
    private PreparedStatement sAllLogs;
    private PreparedStatement sLogByOrder;

    private PreparedStatement sAddLog;
    private PreparedStatement sUpdateLog;
    private PreparedStatement sDeleteLog;
    private static final String TABLE = "LOG_STATO";


    public LogOrderStateDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sLogById = getConnection().prepareStatement("SELECT * FROM"+ TABLE +"WHERE ID=?");
            sLogByOrder = getConnection().prepareStatement("SELECT * FROM"+ TABLE +"WHERE ORDINE_ID=?");
            sAllLogs = getConnection().prepareStatement("SELECT * FROM"+ TABLE);

        sAddLog = getConnection().prepareStatement(
            "INSERT INTO"+ TABLE +"(NOME, STATO_FROM, STATO_TO, ORDINE-ID, UTENTE-ID, VERSION ) VALUES (?,?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            sUpdateLog = getConnection().prepareStatement(
                "UPDATE"+ TABLE + "SET NOME=?, STATO_FROM=?, STATO_TO=?, ORDINE-ID=?, UTENTE-ID=?, VERSION=? WHERE ID=? AND VERSION=?");
            sDeleteLog = getConnection().prepareStatement("DELETE FROM"+ TABLE + "WHERE ID=? AND version=?");
            
        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione LogOrderStateDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if(sLogById != null) sLogById.close();
            if(sLogByOrder != null) sLogByOrder.close();
            if(sAllLogs != null) sAllLogs.close();
            if(sAddLog != null) sAddLog.close();
            if(sUpdateLog != null) sUpdateLog.close();
            if(sDeleteLog != null) sDeleteLog.close();
            super.destroy();
        } catch (SQLException ex) {
            throw new DataException("Errore chiusura LogOrderStateDAO", ex);
        }
    }

    protected LogOrderState createLogOrderState(ResultSet rs) throws SQLException {
        LogOrderStateProxy p = new LogOrderStateProxy(getDataLayer());
        p.setKey(rs.getInt("ID"));
        p.setVersion(rs.getLong("VERSION"));
    
        
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