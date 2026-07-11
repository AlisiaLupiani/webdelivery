package WebMarket.Controller;

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

            String emailInserita = normalize(request.getParameter("email"));
            String passwordInserita = request.getParameter("password");

            DataLayer dl = (DataLayer) request.getAttribute("datalayer");
            UserDAO userDAO = (UserDAO) dl.getDAO(User.class);

            User utente = userDAO.getUserByEmail(emailInserita);

            if (utente != null && isPasswordValid(passwordInserita, utente)) {

                SecurityHelpers.createSession(request, utente);

                response.sendRedirect("home");
                return;

            } else {
                request.setAttribute("errore", "Email o password non validi. Riprova.");
                request.setAttribute("email", emailInserita);

                TemplateResult templateEngine = new TemplateResult(getServletContext());
                templateEngine.activate("login.ftl.html", request, response);
            }

        } else {
            TemplateResult templateEngine = new TemplateResult(getServletContext());
            templateEngine.activate("login.ftl.html", request, response);
        }
    }

    private boolean isPasswordValid(String submittedPassword, User user) {
        // Scelta temporanea di progetto: il database di sviluppo contiene password
        // in chiaro per mantenere accessibili gli utenti di test esistenti.
        return submittedPassword != null && submittedPassword.equals(user.getPassword());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
