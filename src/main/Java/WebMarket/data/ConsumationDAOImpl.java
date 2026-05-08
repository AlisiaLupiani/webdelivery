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
    private PreparedStatement iConsumation;
    private PreparedStatement uConsumation;
    private PreparedStatement dConsumation;

    public ConsumationDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sConsumationById = connection.prepareStatement("SELECT * FROM CONSUMAZIONE WHERE ID = ?");
            sConsumationByPrice = connection.prepareStatement("SELECT * FROM CONSUMAZIONE WHERE PREZZO = ?");
            sAllConsumations = connection.prepareStatement("SELECT * FROM CONSUMAZIONE");
            
            iConsumation = connection.prepareStatement("INSERT INTO CONSUMAZIONE (NOME, PREZZO, VERSION) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uConsumation = connection.prepareStatement("UPDATE CONSUMAZIONE SET NOME=?, PREZZO=?, VERSION=? WHERE ID=? AND VERSION=?");
            dConsumation = connection.prepareStatement("DELETE FROM CONSUMAZIONE WHERE ID=?");
        } catch (SQLException ex) {
            throw new DataException("Errore init ConsumationDAO", ex);
        }
    }

    protected ConsumationProxy createConsumation(ResultSet rs) throws SQLException {
        ConsumationProxy p = new ConsumationProxy(getDataLayer());
        p.setKey(rs.getInt("ID"));
        p.setName(rs.getString("NOME"));
        p.setPrice(rs.getDouble("PREZZO"));
        p.setVersion(rs.getLong("VERSION"));
        p.setClean();
        return p;
    }

    @Override
    public Consumation getConsumationById(Consumation consumation) throws DataException {
        try {
            sConsumationById.setInt(1, consumation.getKey());
            try (ResultSet rs = sConsumationById.executeQuery()) {
                if (rs.next()) {
                    return createConsumation(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
        return null;
    }

    @Override
    public Consumation getConsumationByPrice(Consumation consumation) throws DataException {
        try {
            sConsumationByPrice.setDouble(1, consumation.getPrice());
            try (ResultSet rs = sConsumationByPrice.executeQuery()) {
                if (rs.next()) {
                    return createConsumation(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
        return null;
    }

    @Override
    public List<Consumation> getAllConsumations() throws DataException {
        List<Consumation> res = new ArrayList<>();
        try (ResultSet rs = sAllConsumations.executeQuery()) {
            while (rs.next()) {
                res.add(createConsumation(rs));
            }
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
        return res;
    }

    @Override
    public void addConsumation(Consumation consumation) throws DataException {
        try {
            iConsumation.setString(1, consumation.getName());
            iConsumation.setDouble(2, consumation.getPrice());
            iConsumation.setLong(3, 1L); // Versione iniziale
            if (iConsumation.executeUpdate() == 1) {
                try (ResultSet keys = iConsumation.getGeneratedKeys()) {
                    if (keys.next()) {
                        consumation.setKey(keys.getInt(1));
                        consumation.setVersion(1L);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }

    @Override
    public void updateConsumation(Consumation consumation) throws DataException {
        try {
            long currentVersion = consumation.getVersion();
            long nextVersion = currentVersion + 1;
            uConsumation.setString(1, consumation.getName());
            uConsumation.setDouble(2, consumation.getPrice());
            uConsumation.setLong(3, nextVersion);
            uConsumation.setInt(4, consumation.getKey());
            uConsumation.setLong(5, currentVersion);

            if (uConsumation.executeUpdate() == 0) {
                throw new DataException("Record già modificato (Optimistic Lock fail)");
            }
            consumation.setVersion(nextVersion);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }

    @Override
    public void deleteConsumation(Consumation consumation) throws DataException {
        try {
            dConsumation.setInt(1, consumation.getKey());
            dConsumation.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }
}