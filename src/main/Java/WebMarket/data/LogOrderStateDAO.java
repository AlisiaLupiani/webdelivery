package WebMarket.data;

import java.util.List;

import framework.data.DataException;
import model.LogOrderState;


public interface LogOrderStateDAO {

    LogOrderState getLogOrderStateById(LogOrderState logOrderState) throws DataException;
    LogOrderState getLogOrderStateByOrder(LogOrderState logOrderState) throws DataException;
    LogOrderState getLogOrderStateByStaff(LogOrderState logOrderState) throws DataException;
    LogOrderState getLogOrderStateByDataTime(LogOrderState logOrderState) throws DataException;
    List<LogOrderState> getAllLogOrderStates() throws DataException;

    void addLogOrderState(LogOrderState logOrderState) throws DataException;
    void updatLogOrderState(LogOrderState logOrderState) throws DataException;
    void deleteLogOrderState(LogOrderState logOrderState) throws DataException;


}
