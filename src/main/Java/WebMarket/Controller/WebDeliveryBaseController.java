package WebMarket.Controller;

import javax.sql.DataSource;

import WebMarket.data.WebDeliveryDataLayer;
import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import jakarta.servlet.ServletException;


public abstract class WebDeliveryBaseController extends AbstractBaseController {

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new WebDeliveryDataLayer(ds);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

}
