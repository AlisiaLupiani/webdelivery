package WebMarket.data.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.ProductOptionGroupDAO;
import WebMarket.data.proxy.ProductOptionGroupProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Product;
import model.ProductOptionGroup;

public class ProductOptionGroupDAOImpl extends DAO implements ProductOptionGroupDAO {

    private PreparedStatement sGroupById;
    private PreparedStatement sGroupByProduct;
    private PreparedStatement sAllGroups;
    private PreparedStatement sAddGroup;
    private PreparedStatement sUpdateGroup;
    private PreparedStatement sDeleteGroup;

    private static final String TABLE = "GRUPPO";

    public ProductOptionGroupDAOImpl(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sGroupById = getConnection().prepareStatement(
                    "SELECT * FROM " + TABLE + " WHERE ID=?"
            );

            sGroupByProduct = getConnection().prepareStatement(
                    "SELECT DISTINCT g.* FROM GRUPPO g " +
                    "JOIN CARATTERISTICA c ON g.ID = c.GRUPPO_ID " +
                    "JOIN PRODOTTO_CARATTERISTICA pc ON c.ID = pc.CARATTERISTICA_ID " +
                    "WHERE pc.PRODOTTO_ID = ? " +
                    "ORDER BY g.NOME"
            );

            sAllGroups = getConnection().prepareStatement(
                    "SELECT * FROM " + TABLE + " ORDER BY NOME"
            );

            sAddGroup = getConnection().prepareStatement(
                    "INSERT INTO " + TABLE + " (NOME, SCELTA_SINGOLA, VERSION) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            sUpdateGroup = getConnection().prepareStatement(
                    "UPDATE " + TABLE + " SET NOME=?, SCELTA_SINGOLA=?, VERSION=? WHERE ID=? AND VERSION=?"
            );

            sDeleteGroup = getConnection().prepareStatement(
                    "DELETE FROM " + TABLE + " WHERE ID=? AND VERSION=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductOptionGroupDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sGroupById != null) sGroupById.close();
            if (sGroupByProduct != null) sGroupByProduct.close();
            if (sAllGroups != null) sAllGroups.close();
            if (sAddGroup != null) sAddGroup.close();
            if (sUpdateGroup != null) sUpdateGroup.close();
            if (sDeleteGroup != null) sDeleteGroup.close();
            super.destroy();
        } catch (SQLException e) {
            throw new DataException("Error closing ProductOptionGroupDAO", e);
        }
    }

    protected ProductOptionGroup createProductOptionGroup(ResultSet rs) throws SQLException {
        ProductOptionGroupProxy group = new ProductOptionGroupProxy(getDataLayer());

        group.setKey(rs.getInt("ID"));
        group.setName(rs.getString("NOME"));
        group.setSingleChoice(rs.getBoolean("SCELTA_SINGOLA"));
        group.setVersion(rs.getLong("VERSION"));

        group.setClean();
        return group;
    }

    @Override
    public ProductOptionGroup getProductOptionGroupById(int group_key) throws DataException {
        ProductOptionGroup group = null;

        if (getDataLayer().getCache().has(ProductOptionGroup.class, group_key)) {
            group = getDataLayer().getCache().get(ProductOptionGroup.class, group_key);
        } else {
            try {
                sGroupById.setInt(1, group_key);

                try (ResultSet rs = sGroupById.executeQuery()) {
                    if (rs.next()) {
                        group = createProductOptionGroup(rs);
                        getDataLayer().getCache().add(ProductOptionGroup.class, group);
                    }
                }
            } catch (SQLException e) {
                throw new DataException("Errore getProductOptionGroupById", e);
            }
        }

        return group;
    }

    @Override
    public List<ProductOptionGroup> getAllProductOptionGroups() throws DataException {
        List<ProductOptionGroup> result = new ArrayList<>();

        try (ResultSet rs = sAllGroups.executeQuery()) {
            while (rs.next()) {
                ProductOptionGroup group;
                Integer id = rs.getInt("ID");

                if (getDataLayer().getCache().has(ProductOptionGroup.class, id)) {
                    group = getDataLayer().getCache().get(ProductOptionGroup.class, id);
                } else {
                    group = createProductOptionGroup(rs);
                    getDataLayer().getCache().add(ProductOptionGroup.class, group);
                }

                result.add(group);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to retrieve all product option groups", e);
        }

        return result;
    }

    @Override
    public List<ProductOptionGroup> getProductOptionGroupsByProduct(Product product) throws DataException {
        List<ProductOptionGroup> result = new ArrayList<>();

        try {
            sGroupByProduct.setInt(1, product.getKey());

            try (ResultSet rs = sGroupByProduct.executeQuery()) {
                while (rs.next()) {
                    ProductOptionGroup group;
                    Integer id = rs.getInt("ID");

                    if (getDataLayer().getCache().has(ProductOptionGroup.class, id)) {
                        group = getDataLayer().getCache().get(ProductOptionGroup.class, id);
                    } else {
                        group = createProductOptionGroup(rs);
                        getDataLayer().getCache().add(ProductOptionGroup.class, group);
                    }

                    result.add(group);
                }
            }
        } catch (SQLException e) {
            throw new DataException("Unable to retrieve product option groups by product", e);
        }

        return result;
    }

    @Override
    public void addProductOptionGroup(ProductOptionGroup group) throws DataException {
        try {
            sAddGroup.setString(1, group.getName());
            sAddGroup.setBoolean(2, group.isSingleChoice());

            long initialVersion = 1;
            sAddGroup.setLong(3, initialVersion);

            if (sAddGroup.executeUpdate() == 1) {
                try (ResultSet rs = sAddGroup.getGeneratedKeys()) {
                    if (rs.next()) {
                        group.setKey(rs.getInt(1));
                        group.setVersion(initialVersion);
                    }
                }

                getDataLayer().getCache().add(ProductOptionGroup.class, group);
            }
        } catch (SQLException e) {
            throw new DataException("Unable to add product option group", e);
        }
    }

    @Override
    public void updateProductOptionGroup(ProductOptionGroup group) throws DataException {
        try {
            sUpdateGroup.setString(1, group.getName());
            sUpdateGroup.setBoolean(2, group.isSingleChoice());

            long currentVersion = group.getVersion();
            long nextVersion = currentVersion + 1;

            sUpdateGroup.setLong(3, nextVersion);
            sUpdateGroup.setInt(4, group.getKey());
            sUpdateGroup.setLong(5, currentVersion);

            if (sUpdateGroup.executeUpdate() == 0) {
                throw new DataException("Optimistic locking failed: product option group modified by another process");
            }

            group.setVersion(nextVersion);
            getDataLayer().getCache().add(ProductOptionGroup.class, group);
        } catch (SQLException e) {
            throw new DataException("Unable to update product option group", e);
        }
    }

    @Override
    public void deleteProductOptionGroup(ProductOptionGroup group) throws DataException {
        try {
            sDeleteGroup.setInt(1, group.getKey());
            sDeleteGroup.setLong(2, group.getVersion());

            if (sDeleteGroup.executeUpdate() > 0) {
                getDataLayer().getCache().delete(ProductOptionGroup.class, group.getKey());
            }
        } catch (SQLException e) {
            throw new DataException("Unable to delete product option group", e);
        }
    }
}
