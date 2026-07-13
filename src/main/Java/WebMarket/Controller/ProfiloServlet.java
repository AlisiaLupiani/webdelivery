package WebMarket.Controller;

import java.util.List;

import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import framework.security.SecurityHelpers;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Client;
import model.Order;
import model.User;

@WebServlet(name = "ProfiloServlet", urlPatterns = {"/profilo"})
public class ProfiloServlet extends WebDeliveryBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = Integer.parseInt(session.getAttribute("userid").toString());

        DataLayer dl = (DataLayer) request.getAttribute("datalayer");
        UserDAO userDAO = (UserDAO) dl.getDAO(User.class);
        OrderDAO orderDAO = (OrderDAO) dl.getDAO(Order.class);

        User user = userDAO.getUserById(userId);

        if (!(user instanceof Client)) {
            response.sendRedirect("home");
            return;
        }

        Client cliente = (Client) user;

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String action = normalize(request.getParameter("action"));

            if ("password".equals(action)) {
                cambiaPassword(request, response, userDAO, orderDAO, cliente);
            } else {
                aggiornaProfilo(request, response, userDAO, orderDAO, cliente, session);
            }

            return;
        }

        mostraProfilo(request, response, orderDAO, cliente);
    }

    private void aggiornaProfilo(
            HttpServletRequest request,
            HttpServletResponse response,
            UserDAO userDAO,
            OrderDAO orderDAO,
            Client cliente,
            HttpSession session) throws Exception {

        String nome = normalize(request.getParameter("nome"));
        String cognome = normalize(request.getParameter("cognome"));
        String email = normalize(request.getParameter("email"));
        String indirizzo = normalize(request.getParameter("indirizzo"));
        String telefono = normalize(request.getParameter("telefono"));

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || indirizzo.isEmpty() || telefono.isEmpty()) {
            request.setAttribute("errore", "Compila tutti i campi del profilo.");
            mostraProfilo(request, response, orderDAO, cliente);
            return;
        }

        User utenteConEmail = userDAO.getUserByEmail(email);

        if (utenteConEmail != null && !utenteConEmail.getKey().equals(cliente.getKey())) {
            request.setAttribute("errore", "Questa email e' gia' usata da un altro account.");
            mostraProfilo(request, response, orderDAO, cliente);
            return;
        }

        cliente.setName(nome);
        cliente.setSurname(cognome);
        cliente.setEmail(email);
        cliente.setAddress(indirizzo);
        cliente.setPhone(telefono);

        userDAO.updateUser(cliente);

        session.setAttribute("username", cliente.getEmail());
        session.setAttribute("name", cliente.getName());
        session.setAttribute("surname", cliente.getSurname());

        response.sendRedirect("profilo?success=profile");
    }

    private void cambiaPassword(
            HttpServletRequest request,
            HttpServletResponse response,
            UserDAO userDAO,
            OrderDAO orderDAO,
            Client cliente) throws Exception {

        String vecchiaPassword = request.getParameter("vecchia_password");
        String nuovaPassword = request.getParameter("nuova_password");
        String confermaPassword = request.getParameter("conferma_password");

        if (vecchiaPassword == null || nuovaPassword == null || confermaPassword == null
                || vecchiaPassword.isBlank() || nuovaPassword.isBlank() || confermaPassword.isBlank()) {
            request.setAttribute("errore", "Compila tutti i campi della password.");
            mostraProfilo(request, response, orderDAO, cliente);
            return;
        }

        if (!isPasswordValid(vecchiaPassword, cliente.getPassword())) {
            request.setAttribute("errore", "La vecchia password non e' corretta.");
            mostraProfilo(request, response, orderDAO, cliente);
            return;
        }

        if (!nuovaPassword.equals(confermaPassword)) {
            request.setAttribute("errore", "La nuova password e la conferma non coincidono.");
            mostraProfilo(request, response, orderDAO, cliente);
            return;
        }

        cliente.setPassword(SecurityHelpers.getPasswordHashPBKDF2(nuovaPassword));
        userDAO.updateUser(cliente);

        response.sendRedirect("profilo?success=password");
    }

    private void mostraProfilo(
            HttpServletRequest request,
            HttpServletResponse response,
            OrderDAO orderDAO,
            Client cliente) throws Exception {

        List<Order> ordini = orderDAO.getOrdersByClient(cliente);

        String success = normalize(request.getParameter("success"));

        if ("profile".equals(success)) {
            request.setAttribute("successo", "Profilo aggiornato correttamente.");
        } else if ("password".equals(success)) {
            request.setAttribute("successo", "Password modificata correttamente.");
        }

        request.setAttribute("cliente", cliente);
        request.setAttribute("ordini", ordini);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("profilo.ftl.html", request, response);
    }

    private boolean isPasswordValid(String submittedPassword, String storedPassword) throws Exception {
        if (submittedPassword == null || storedPassword == null) {
            return false;
        }

        if (isPBKDF2Hash(storedPassword)) {
            return SecurityHelpers.checkPasswordHashPBKDF2(submittedPassword, storedPassword);
        }

        // Compatibilita' temporanea con gli utenti vecchi ancora salvati in chiaro.
        return submittedPassword.equals(storedPassword);
    }

    private boolean isPBKDF2Hash(String password) {
        return password != null && password.matches("^[0-9a-fA-F]{96}$");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}