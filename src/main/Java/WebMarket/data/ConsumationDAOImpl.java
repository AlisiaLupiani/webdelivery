package WebMarket.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
            
            // Query con JOIN perché il prezzo è nella tabella PRODOTTO
            sConsumationByPrice = getConnection().prepareStatement(
                "SELECT c.* FROM " + TABLE + " c " +
                "JOIN PRODOTTO p ON c.PRODOTTO_ID = p.ID " +
                "WHERE p.PREZZO = ?"
            );
            
            sAllConsumations = getConnection().prepareStatement("SELECT * FROM " + TABLE);

            sAddConsumation = getConnection().prepareStatement(
                "INSERT INTO " + TABLE + " (NOME, DESCRIZIONE, PRODOTTO_ID, VERSION) VALUES (?, ?, ?, ?)", 
                Statement.RETURN_GENERATED_KEYS);

            sUpdateConsumation = getConnection().prepareStatement(
                "UPDATE " + TABLE + " SET NOME = ?, DESCRIZIONE = ?, PRODOTTO_ID = ?, VERSION = ? WHERE ID = ? AND VERSION = ?");

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
        c.setVersion(rs.getLong("VERSION"));
        
        c.setClean();
        return c;
    }

   @Override
    public Consumation getConsumationById(Consumation consumation) throws DataException {
        int veroId = consumation.getKey();

        if (getDataLayer().getCache().has(Consumation.class, veroId)) {
            return getDataLayer().getCache().get(Consumation.class, veroId);
        }

        try {
            sConsumationById.setInt(1, veroId);
            try (ResultSet rs = sConsumationById.executeQuery()) {
                if (rs.next()) {
                    Consumation c = createConsumation(rs);
                    getDataLayer().getCache().add(Consumation.class, c);
                    return c;
                }
            }
        } catch (SQLException e) {
            throw new DataException("Errore esecuzione query in getConsumationById", e);
        }
        
        throw new DataException("Impossibile trovare la consumazione nel DB!");
    }

    @Override
    public Consumation getConsumationByPrice(double price) throws DataException {
        Consumation consumation = null;
        if(getDataLayer().getCache().has(Consumation.class, consumation.getKey())){
            consumation = getDataLayer().getCache().get(Consumation.class, consumation.getKey());
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
        }
        catch (SQLException e) {
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
                result.add(createConsumation(rs));
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
            
            long initialVersion = 1;
            sAddConsumation.setLong(4, initialVersion);

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
            sUpdateConsumation.setLong(4, nextVersion);
            sUpdateConsumation.setInt(5, consumation.getKey());
            sUpdateConsumation.setLong(6, currentVersion);

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