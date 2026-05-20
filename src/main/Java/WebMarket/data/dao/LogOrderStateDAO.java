package WebMarket.data.dao;

import java.util.List;

import framework.data.DataException;
import model.LogOrderState;
import model.Order;

public interface LogOrderStateDAO {

    LogOrderState getLogOrderStateById(int log_key) throws DataException;

    List<LogOrderState> getAllLogOrderStates() throws DataException;

    List<LogOrderState> getLogOrderStateByOrder(Order order) throws DataException;

    void addLogOrderState(LogOrderState log) throws DataException;

    void updateLogOrderState(LogOrderState log) throws DataException;

    void deleteLogOrderState(LogOrderState log) throws DataException;
}