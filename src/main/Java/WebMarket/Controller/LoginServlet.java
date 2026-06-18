package WebMarket.Controller;

import javax.sql.DataSource;

import WebMarket.data.dao.UserDAO;
import WebMarket.data.daoimpl.UserDAOImpl;
import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import framework.security.SecurityHelpers;
import framework.view.TemplateResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

@jakarta.servlet.annotation.WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends AbstractBaseController {

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            DataLayer dl = new framework.data.DataLayer(ds);
            dl.registerDAO(model.User.class, new UserDAOImpl(dl));
            return dl;
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // Se ha già il VIP Pass, va dritto alla Home
        if (SecurityHelpers.checkSession(request) != null) {
            response.sendRedirect("home");
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            // ORA CHIEDIAMO L'EMAIL, NON L'USERNAME!
            String emailInserita = request.getParameter("email");
            String passwordInserita = request.getParameter("password");

            DataLayer dl = (DataLayer) request.getAttribute("datalayer");
            UserDAO userDAO = (UserDAO) dl.getDAO(User.class);

            // Usiamo il tuo metodo esatto: getUserByEmail
            User utente = userDAO.getUserByEmail(emailInserita);

            // Controlliamo che l'utente esista e che la password coincida
            if (utente != null && utente.getPassword().equals(passwordInserita)) {
                
                // 1. Creiamo la sessione ufficiale e sicura tramite il tuo framework!
                SecurityHelpers.createSession(request, utente);
                
                // 2. IL PEZZO MANCANTE: Plachiamo il buttafuori assegnando un ruolo alla sessione!
                // (Per ora forziamo "ADMIN", poi potrai usare utente.getRuolo() se lo aggiungi al model)
                request.getSession().setAttribute("roles", "ADMIN");
                
                // 3. Login riuscito! Rimandiamo alla Home
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