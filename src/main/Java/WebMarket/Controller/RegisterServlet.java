package WebMarket.Controller;

import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import framework.security.SecurityHelpers;
import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.modelImpl.ClientImpl;

@jakarta.servlet.annotation.WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends WebDeliveryBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (SecurityHelpers.checkSession(request) != null) {
            response.sendRedirect("home");
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            registerClient(request, response);
        } else {
            renderForm(request, response);
        }
    }

    private void registerClient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = normalize(request.getParameter("nome"));
        String surname = normalize(request.getParameter("cognome"));
        String email = normalize(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("conferma_password");
        String address = normalize(request.getParameter("indirizzo"));
        String phone = normalize(request.getParameter("telefono"));

        keepFormValues(request, name, surname, email, address, phone);

        if (name.isBlank() || surname.isBlank() || email.isBlank()
                || isBlank(password) || address.isBlank() || phone.isBlank()) {
            request.setAttribute("errore", "Compila tutti i campi obbligatori.");
            renderForm(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errore", "Le password inserite non coincidono.");
            renderForm(request, response);
            return;
        }

        DataLayer dl = (DataLayer) request.getAttribute("datalayer");
        UserDAO userDAO = (UserDAO) dl.getDAO(User.class);

        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("errore", "Esiste gia' un utente registrato con questa email.");
            renderForm(request, response);
            return;
        }

        ClientImpl client = new ClientImpl();
        client.setName(name);
        client.setSurname(surname);
        client.setEmail(email);
      client.setPassword(SecurityHelpers.getPasswordHashPBKDF2(password));
        client.setAddress(address);
        client.setPhone(phone);

        User registeredUser = userDAO.addUser(client);
        SecurityHelpers.createSession(request, registeredUser);
        response.sendRedirect("home");
    }

    private void renderForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("register.ftl.html", request, response);
    }

    private void keepFormValues(HttpServletRequest request, String name, String surname, String email, String address, String phone) {
        request.setAttribute("nome", name);
        request.setAttribute("cognome", surname);
        request.setAttribute("email", email);
        request.setAttribute("indirizzo", address);
        request.setAttribute("telefono", phone);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
