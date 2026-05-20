package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.LogOrderStateDAO;
import WebMarket.data.proxy.LogOrderStateProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.LogOrderState;
import model.Order;
import model.OrderState;




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

            sLogById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
            sAllLogs = getConnection().prepareStatement("SELECT * FROM " + TABLE);
            sLogByOrder = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ORDINE_ID = ? ORDER BY TIMESTAMP DESC");

            sAddLog = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + " (TIMESTAMP, STATO_FROM, STATO_TO, ORDINE_ID, UTENTE_ID, VERSION) VALUES (?, ?, ?, ?, ?, ?)", 
                Statement.RETURN_GENERATED_KEYS);

            sUpdateLog = getConnection().prepareStatement(
                "UPDATE " + TABLE + " SET TIMESTAMP = ?, STATO_FROM = ?, STATO_TO = ?, ORDINE_ID = ?, UTENTE_ID = ?, VERSION = ? WHERE ID = ? AND VERSION = ?");

            sDeleteLog = getConnection().prepareStatement("DELETE FROM " + TABLE + " WHERE ID = ?");

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione LogOrderStateDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sLogById != null) sLogById.close();
            if (sAllLogs != null) sAllLogs.close();
            if (sLogByOrder != null) sLogByOrder.close();
            if (sAddLog != null) sAddLog.close();
            if (sUpdateLog != null) sUpdateLog.close();
            if (sDeleteLog != null) sDeleteLog.close();
            super.destroy();
        } catch (SQLException ex) {
            throw new DataException("Errore chiusura LogOrderStateDAO", ex);
        }
    }

    protected LogOrderState createLogOrderState(ResultSet rs) throws SQLException {

        LogOrderStateProxy log = new LogOrderStateProxy(getDataLayer());
        log.setKey(rs.getInt("ID"));
        log.setDateTime(rs.getTimestamp("TIMESTAMP").toLocalDateTime());
         
        String statoFromDalDb = rs.getString("STATO_FROM");
    if (statoFromDalDb != null) {
    
        log.setStateFrom(OrderState.valueOf(statoFromDalDb));
    }
        String statoToDalDb = rs.getString("STATO_TO");
        if (statoToDalDb != null) {
            log.setStateTo(OrderState.valueOf(statoToDalDb));
        
        log.setIdOrder(rs.getInt("ORDINE_ID")); 
        log.setIdStaff(rs.getInt("UTENTE_ID"));  
        log.setVersion(rs.getLong("VERSION"));
        
        log.setClean();
        }

        return log;
    }

    @Override
    public LogOrderState getLogOrderStateById(int log_key) throws DataException {
        LogOrderState log = null;
        if (getDataLayer().getCache().has(LogOrderState.class, log_key)) {
            log = getDataLayer().getCache().get(LogOrderState.class, log_key);
        }
        try {
            sLogById.setInt(1, log_key);
            try (ResultSet rs = sLogById.executeQuery()) {
                if (rs.next()) {
                    log = createLogOrderState(rs);
                    getDataLayer().getCache().add(LogOrderState.class, log);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore recupero Log per ID", e);
        }
        return log;
    }

    @Override
    public List<LogOrderState> getAllLogOrderStates() throws DataException {

        List<LogOrderState> result = new ArrayList<>();
        try (ResultSet rs = sAllLogs.executeQuery()) {
            while (rs.next()) {
                result.add(createLogOrderState(rs));
            }
        } catch (SQLException ex) {
            throw new DataException("Errore recupero tutti i log", ex);
        }
        return result;
    }

    @Override
    public List<LogOrderState> getLogOrderStateByOrder(Order order) throws DataException {
        
        List<LogOrderState> result = new ArrayList<>();
        try {
            sLogByOrder.setInt(1, order.getKey());
            try (ResultSet rs = sLogByOrder.executeQuery()) {
                while (rs.next()) {
                    result.add(createLogOrderState(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore recupero log per ordine", ex);
        }
        return result;
    }

    @Override
    public void addLogOrderState(LogOrderState log) throws DataException {
        try {
            sAddLog.setTimestamp(1, Timestamp.valueOf(log.getDateTime().toString()));
            sAddLog.setString(2, log.getStateFrom().name());
            sAddLog.setString(3, log.getStateTo().name());
            sAddLog.setInt(4, log.getOrder().getKey());
            sAddLog.setInt(5, log.getStaff().getKey());
            
            long initialVersion = 1;
            sAddLog.setLong(6, initialVersion);

            if (sAddLog.executeUpdate() == 1) {
                try (ResultSet rs = sAddLog.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setKey(rs.getInt(1));
                        log.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(LogOrderState.class, log);
            }
        } catch (SQLException e) {
            throw new DataException("Errore inserimento log", e);
        }
    }

    @Override
    public void updateLogOrderState(LogOrderState log) throws DataException {
        try {
            long currentVersion = log.getVersion();
            long nextVersion = currentVersion + 1;

            sUpdateLog.setTimestamp(1, Timestamp.valueOf(log.getDateTime().toString()));
            sUpdateLog.setString(2, log.getStateFrom().toString());
            sUpdateLog.setString(3, log.getStateTo().toString());
            sUpdateLog.setInt(4, log.getOrder().getKey());
            sUpdateLog.setInt(5, log.getStaff().getKey());
            sUpdateLog.setLong(6, nextVersion);
            sUpdateLog.setInt(7, log.getKey());
            sUpdateLog.setLong(8, currentVersion);

            if (sUpdateLog.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed su LogOrderState");
            } else {
                log.setVersion(nextVersion);
            }
        } catch (SQLException e) {
            throw new DataException("Errore aggiornamento log", e);
        }
    }

    @Override
    public void deleteLogOrderState(LogOrderState log) throws DataException {
        try {
            sDeleteLog.setInt(1, log.getKey());
            if (sDeleteLog.executeUpdate() > 0) {
                getDataLayer().getCache().delete(LogOrderState.class, log.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Errore eliminazione log", e);
        }
    }
}