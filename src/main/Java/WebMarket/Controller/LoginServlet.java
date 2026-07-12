package WebMarket.Controller;

import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import framework.security.SecurityHelpers;
import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Proprietor;
import model.Staff;
import model.User;

@jakarta.servlet.annotation.WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends WebDeliveryBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = SecurityHelpers.checkSession(request);

        if (session != null) {
            response.sendRedirect(getRedirectByRoles(session));
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

                response.sendRedirect(getRedirectByUser(utente));
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

    private String getRedirectByUser(User user) {
        if (user instanceof Proprietor) {
            return "admin";
        }

        if (user instanceof Staff) {
            return "staff";
        }

        return "home";
    }

    private String getRedirectByRoles(HttpSession session) {
        Object rolesObj = session.getAttribute("roles");

        if (rolesObj instanceof java.util.List<?>) {
            java.util.List<?> roles = (java.util.List<?>) rolesObj;

            if (roles.contains("ADMIN")) {
                return "admin";
            }

            if (roles.contains("STAFF")) {
                return "staff";
            }
        }

        return "home";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}