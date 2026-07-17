package WebMarket.Controller;

import javax.sql.DataSource;

import WebMarket.data.WebDeliveryDataLayer;
import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;


public abstract class WebDeliveryBaseController extends AbstractBaseController {

    @Override
    protected void initRequest(HttpServletRequest request, DataLayer dl) {
        super.initRequest(request, dl);
        if ("GET".equalsIgnoreCase(request.getMethod()) && request.getSession(false) != null) {
            prepareCsrfToken(request);
        }
    }

    protected boolean checkCsrf(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        String csrfForm = request.getParameter("csrf");
        Object csrfSession = session != null ? session.getAttribute("csrf") : null;

        if (csrfSession == null || csrfForm == null || !csrfForm.equals(csrfSession)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF non valido.");
            return false;
        }

        session.removeAttribute("csrf");
        prepareCsrfToken(request);
        return true;
    }

    private void prepareCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String csrfToken = UUID.randomUUID().toString();
            session.setAttribute("csrf", csrfToken);
            request.setAttribute("csrf", csrfToken);
        }
    }

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new WebDeliveryDataLayer(ds);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

}
