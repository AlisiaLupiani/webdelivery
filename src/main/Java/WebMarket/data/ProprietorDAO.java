package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.Proprietor;


public interface ProprietorDAO {

    Proprietor getProprietorById(int id) throws DataException;
    Proprietor getProprietorByEmail(String name) throws DataException;
    List<Proprietor> getAllProprietors() throws DataException;

    void addProprietor(Proprietor proprietor) throws DataException;
    void updateProprietor(Proprietor proprietor) throws DataException;
    void deleteProprietor(Proprietor proprietor) throws DataException;


}
