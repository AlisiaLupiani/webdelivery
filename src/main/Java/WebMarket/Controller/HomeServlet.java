package WebMarket.Controller;

import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@jakarta.servlet.annotation.WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends AbstractBaseController {

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new framework.data.DataLayer(ds);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // ECCO LA MAGIA: Inseriamo il testo nella variabile che l'HTML sta aspettando!
            request.setAttribute("messaggio_benvenuto", "Ciao! Il motore Java e i template stanno comunicando alla perfezione! 🚀");
            
            TemplateResult templateEngine = new TemplateResult(getServletContext());
            templateEngine.activate("home.ftl.html", request, response);
            
        } catch (Exception ex) {
            handleError(ex, request, response);
        }
    }
} 