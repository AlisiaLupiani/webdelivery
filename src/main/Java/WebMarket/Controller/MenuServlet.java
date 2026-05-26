package WebMarket.Controller;

import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import WebMarket.data.dao.ProductDAO;
import model.Product;

// ECCO IL NUOVO IMPORT CHE SERVE PER IL CACCIAVITE:
import WebMarket.data.daoimpl.ProductDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import javax.sql.DataSource;

@jakarta.servlet.annotation.WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends AbstractBaseController {

    // 1. IL METODO AGGIORNATO CHE "RIEMPIE LA CASSETTA DEGLI ATTREZZI"
    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            // Creo la cassetta vuota
            DataLayer dl = new framework.data.DataLayer(ds);
            
            // Ci metto dentro l'attrezzo per le Pizze (ProductDAOImpl)
            dl.registerDAO(model.Product.class, new WebMarket.data.daoimpl.ProductDAOImpl(dl));
            
            // Restituisco la cassetta pronta a Tomcat!
            return dl;
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    // 2. IL CUORE DELLA SERVLET (rimasto identico al tuo)
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            DataLayer dl = (DataLayer) request.getAttribute("datalayer");
            
            // Ora Tomcat troverà l'attrezzo e non andrà più in null!
            ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
            
            List<Product> listaPizze = productDAO.getAllProducts();
            request.setAttribute("lista_prodotti", listaPizze);

            TemplateResult templateEngine = new TemplateResult(getServletContext());
            templateEngine.activate("menu.ftl.html", request, response);

        } catch (Exception ex) {
            handleError(ex, request, response);
        }
    }
}