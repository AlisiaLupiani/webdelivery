package WebMarket.Controller;

import java.util.List;

import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import framework.security.SecurityHelpers;
import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

@jakarta.servlet.annotation.WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends WebDeliveryBaseController {


    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (SecurityHelpers.checkSession(request) != null) {
            response.sendRedirect("home");
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {

            String emailInserita = request.getParameter("email");
            String passwordInserita = request.getParameter("password");

            DataLayer dl = (DataLayer) request.getAttribute("datalayer");
            UserDAO userDAO = (UserDAO) dl.getDAO(User.class);

            User utente = userDAO.getUserByEmail(emailInserita);

            if (utente != null && utente.getPassword().equals(passwordInserita)) {

                SecurityHelpers.createSession(request, utente);

                // Dati corretti usati anche dal carrello persistente
                request.getSession().setAttribute("userid", utente.getKey());
                request.getSession().setAttribute("username", emailInserita);

                // AbstractBaseController si aspetta una List<String>, non una String
                request.getSession().setAttribute("roles", List.of("ADMIN"));

                response.sendRedirect("home");

            } else {
                request.setAttribute("errore", "Email o password non validi. Riprova.");

                TemplateResult templateEngine = new TemplateResult(getServletContext());
                templateEngine.activate("login.ftl.html", request, response);
            }

        } else {
            TemplateResult templateEngine = new TemplateResult(getServletContext());
            templateEngine.activate("login.ftl.html", request, response);
        }
    }
}