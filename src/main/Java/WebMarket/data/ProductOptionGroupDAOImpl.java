package WebMarket.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import WebMarket.data.proxy.ProductOptionGroupProxy;
import framework.data.DAO;
import framework.data.DataException;
import framework.data.DataLayer;
import model.Product;
import model.ProductOptionGroup;

public class ProductOptionGroupDAOImpl extends DAO implements ProductOptionGroupDAO {

    private PreparedStatement sGroupById;
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
            sGroupById = getConnection().prepareStatement("SELECT * FROM" + TABLE + "WHERE ID=?");
            sAllGroups = getConnection().prepareStatement("SELECT * FROM" + TABLE);
            
            sAddGroup = getConnection().prepareStatement("INSERT INTO"+ TABLE + "(NOME, VERSION) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            sUpdateGroup = getConnection().prepareStatement("UPDATE"+ TABLE + "SET NOME=?, VERSION=? WHERE ID=? AND VERSION=?");
            sDeleteGroup = getConnection().prepareStatement("DELETE FROM"+ TABLE + "WHERE ID=? AND version=?");


        }catch (SQLException ex) {
            throw new DataException("Errore inizializzazione ProductOptionGroupDAO", ex);
        }
          
    }

    @Override
    public void destroy() throws DataException {
        try {
            if(sGroupById != null) sGroupById.close();
            if(sAllGroups != null) sAllGroups.close();
            if(sAddGroup != null) sAddGroup.close();
            if(sUpdateGroup != null) sUpdateGroup.close();
            if(sDeleteGroup != null) sDeleteGroup.close();
            super.destroy();

        } catch (SQLException e) {
            throw new DataException("Error closing WebMarket data layer", e);
        }
    }



    protected ProductOptionGroup createProductOptionGroup(ResultSet rs) throws SQLException {
        ProductOptionGroupProxy p = new ProductOptionGroupProxy(getDataLayer());
        p.setKey(rs.getInt("ID"));
        p.setName(rs.getString("NOME"));
        p.setVersion(rs.getLong("VERSION"));

        p.setClean();
        return p;    
    }

    @Override
    public ProductOptionGroup getProductOptionGroupById(int group_key) throws DataException {
        ProductOptionGroup group = null;
        if(getDataLayer().getCache().has(ProductOptionGroup.class, group_key)){
            group = getDataLayer().getCache().get(ProductOptionGroup.class, group_key);
        }else{
            try{
                sGroupById.setInt(1, group_key);
                try(ResultSet rs = sGroupById.executeQuery()){
                    if(rs.next()){
                        group = createProductOptionGroup(rs);
                        getDataLayer().getCache().add(ProductOptionGroup.class, group);
                    }
                }
            }catch(SQLException e){
                throw new DataException("Errore getProductOptionGroupById", e);
            }

        }
        return group;
    }

    @Override
    public List<ProductOptionGroup> getAllProductOptionGroups() throws DataException {
        List<ProductOptionGroup> res = new ArrayList<>();
        try(ResultSet resultSet = sAllGroups.executeQuery()) {
            while(resultSet.next()) {
                ProductOptionGroup group;
                Integer id = resultSet.getInt("ID");
                if(getDataLayer().getCache().has(ProductOptionGroup.class, id)) {
                    group = getDataLayer().getCache().get(ProductOptionGroup.class, id);
                } else {
                    group = createProductOptionGroup(resultSet);
                    getDataLayer().getCache().add(ProductOptionGroup.class, group);
                }
                res.add(group);
            }
            
        } catch (SQLException e) {
            throw new DataException("Unable to retrieve all product option groups", e);
        }
        return res;
    }

    @Override public void addProductOptionGroup(ProductOptionGroup group) throws DataException {
        try{
            sAddGroup.setString(1, group.getName());

            long initialVersion = 1;
            sAddGroup.setLong(2, initialVersion);

            if(sAddGroup.executeUpdate() == 1){
                try(ResultSet resultSet = sAddGroup.getGeneratedKeys()){
                    if(resultSet.next()){
                        Integer newKey = resultSet.getInt(1);
                        group.setKey(newKey);
                        group.setVersion(initialVersion);
                    }
                }
                getDataLayer().getCache().add(ProductOptionGroup.class, group);
            
            }
        }catch(SQLException e){
            throw new DataException("Unable to add product option group", e);
        }
    }

    @Override public void updateProductOptionGroup(ProductOptionGroup group) throws DataException {
        try{
            sUpdateGroup.setString(1, group.getName());

            long currentVersion = group.getVersion();
            long nextVersion = currentVersion + 1;
            sUpdateGroup.setLong(2, nextVersion);
            sUpdateGroup.setInt(3, group.getKey());
            sUpdateGroup.setLong(4, currentVersion);

            if(sUpdateGroup.executeUpdate() == 0){
                throw new DataException("Optimistic locking failed: product option group modified by another process");
            }else{
                group.setVersion(nextVersion);
            }
        }catch(SQLException e){
            throw new DataException("Unable to update product option group", e);
        }
    }


    @Override public void deleteProductOptionGroup(ProductOptionGroup group) throws DataException {
        try{
            sDeleteGroup.setInt(1, group.getKey());
            if(sDeleteGroup.executeUpdate() > 0){
                getDataLayer().getCache().delete(ProductOptionGroup.class, group.getKey());
            
            }
        }catch(SQLException e){
            throw new DataException("Unable to delete product option group", e);
        }
    }




    
}