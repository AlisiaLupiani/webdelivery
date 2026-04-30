package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.Consumation;

public interface ComsumationDAO {
    Consumation getConsumationById(Consumation consumation) throws DataException;
    Consumation getConsumationByPrice(Consumation consumation) throws DataException;
    List<Consumation> getAllConsumations() throws DataException;

    void addConsumation(Consumation consumation) throws DataException;
    void updateConsumation(Consumation consumation) throws DataException;
    void deleteConsumation(Consumation consumation) throws DataException;


}

