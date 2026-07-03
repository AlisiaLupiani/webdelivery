package WebMarket.Controller;

import javax.sql.DataSource;

import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import jakarta.servlet.ServletException;


public abstract class WebDeliveryBaseController extends AbstractBaseController {

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            DataLayer dl = new framework.data.DataLayer(ds);

            dl.init();

            return dl;
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

}
