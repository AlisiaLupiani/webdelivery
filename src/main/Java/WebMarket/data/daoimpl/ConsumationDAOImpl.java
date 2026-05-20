package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.ConsumationDAO;
import WebMarket.data.proxy.ConsumationProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Consumation;



public class ConsumationDAOImpl extends DAO implements ConsumationDAO {

    private PreparedStatement sConsumationById;
    private PreparedStatement sConsumationByPrice;
    private PreparedStatement sAllConsumations;
    private PreparedStatement sAddConsumation;
    private PreparedStatement sUpdateConsumation;
    private PreparedStatement sDeleteConsumation;

    private static final String TABLE = "CONSUMAZIONE";

    public ConsumationDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sConsumationById = getConnection().prepareStatement("SELECT * FROM " + TABLE + " WHERE ID = ?");
            sConsumationByPrice = getConnection().prepareStatement("SELECT * FROM" + TABLE + "WHERE PREZZO = ?");
            
            sAllConsumations = getConnection().prepareStatement("SELECT * FROM " + TABLE);

            sAddConsumation = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + " (NOME, PREZZO, VERSION) VALUES (?, ?, ?)", 
                Statement.RETURN_GENERATED_KEYS);

            sUpdateConsumation = getConnection().prepareStatement(
                "UPDATE " + TABLE + " SET NOME = ?, PREZZO = ?, VERSION = ? WHERE ID = ? AND VERSION = ?");

            sDeleteConsumation = getConnection().prepareStatement("DELETE FROM " + TABLE + " WHERE ID = ?");

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ConsumationDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sConsumationById != null) sConsumationById.close();
            if (sConsumationByPrice != null) sConsumationByPrice.close();
            if (sAllConsumations != null) sAllConsumations.close();
            if (sAddConsumation != null) sAddConsumation.close();
            if (sUpdateConsumation != null) sUpdateConsumation.close();
            if (sDeleteConsumation != null) sDeleteConsumation.close();
            super.destroy();
        } catch (SQLException ex) {
            throw new DataException("Errore chiusura ConsumationDAO", ex);
        }
    }

    protected Consumation createConsumation(ResultSet rs) throws SQLException {
        ConsumationProxy c = new ConsumationProxy(getDataLayer());
        c.setKey(rs.getInt("ID"));
        c.setName(rs.getString("NOME"));
        c.setPrice(rs.getDouble("PREZZO"));
        c.setVersion(rs.getLong("VERSION"));
        
        c.setClean();
        return c;
    }

   @Override
    public Consumation getConsumationById(int id) throws DataException {
        Consumation consumation = null;

        if (getDataLayer().getCache().has(Consumation.class, id)) {
            consumation = getDataLayer().getCache().get(Consumation.class, id);
            
        }else{
            try {
                sConsumationById.setInt(1, id);
                try (ResultSet rs = sConsumationById.executeQuery()) {
                    if (rs.next()) {
                        consumation = createConsumation(rs);
                        getDataLayer().getCache().add(Consumation.class, consumation);
                    
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Errore esecuzione query in getConsumationById", e);
            }
        }
        return consumation;
    }

    @Override
    public Consumation getConsumationByPrice(double price) throws DataException {

        Consumation consumation = null;
        if(getDataLayer().getCache().has(Consumation.class, price)){
            consumation = getDataLayer().getCache().get(Consumation.class, price);
        }
        else{
            try {
                sConsumationByPrice.setDouble(1, price);
                try (ResultSet rs = sConsumationByPrice.executeQuery()) {
                    if (rs.next()) {
                        consumation = createConsumation(rs);
                        getDataLayer().getCache().add(Consumation.class, consumation);
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Errore getConsumationByPrice", e);
            }
        }
        return consumation;
    }

    @Override
    public List<Consumation> getAllConsumations() throws DataException {
        List<Consumation> result = new ArrayList<>();
        try (ResultSet rs = sAllConsumations.executeQuery()) {
            while (rs.next()) {
                Consumation consumation;
                Integer id = rs.getInt("ID");

                if(getDataLayer().getCache().has(Consumation.class, id)){
                    consumation = getDataLayer().getCache().get(Consumation.class, id);
                }else{
                    consumation = createConsumation(rs);
                }
                result.add(consumation);
            }
        } catch (SQLException ex) {
            throw new DataException("Errore getAllConsumations", ex);
        }
        return result;
    }

    @Override
    public void addConsumation(Consumation consumation) throws DataException {
        try {
            sAddConsumation.setString(1, consumation.getName());
            sAddConsumation.setDouble(2, consumation.getPrice());
            
            long initialVersion = 1;
            sAddConsumation.setLong(3, initialVersion);

            if (sAddConsumation.executeUpdate() == 1) {
                try (ResultSet rs = sAddConsumation.getGeneratedKeys()) {
                    if (rs.next()) {
                        consumation.setKey(rs.getInt(1));
                        consumation.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(Consumation.class, consumation);
            }
        } catch (SQLException e) {
            throw new DataException("Errore addConsumation", e);
        }
    }

    @Override
    public void updateConsumation(Consumation consumation) throws DataException {
        try {
            long currentVersion = consumation.getVersion();
            long nextVersion = currentVersion + 1;

            sUpdateConsumation.setString(1, consumation.getName());
            sUpdateConsumation.setDouble(2, consumation.getPrice());
            sUpdateConsumation.setLong(3, nextVersion);
            sUpdateConsumation.setInt(4, consumation.getKey());
            sUpdateConsumation.setLong(5, currentVersion);

            if (sUpdateConsumation.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed su Consumation");
            } else {
                consumation.setVersion(nextVersion);
            }
        } catch (SQLException e) {
            throw new DataException("Errore updateConsumation", e);
        }
    }

    @Override
    public void deleteConsumation(Consumation consumation) throws DataException {
        try {
            sDeleteConsumation.setInt(1, consumation.getKey());
            if (sDeleteConsumation.executeUpdate() > 0) {
                getDataLayer().getCache().delete(Consumation.class, consumation.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Errore deleteConsumation", e);
        }
    }
}