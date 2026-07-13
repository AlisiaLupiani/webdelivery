package WebMarket.Controller;

import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Staff;
import model.User;
import model.modelImpl.StaffImpl;

@WebServlet(name = "AdminStaffServlet", urlPatterns = {"/admin-staff"})
public class AdminStaffServlet extends WebDeliveryBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login");
            return;
        }

        if (!checkRole(request, "ADMIN")) {
            response.sendRedirect("home");
            return;
        }

        DataLayer dl = (DataLayer) request.getAttribute("datalayer");
        UserDAO userDAO = (UserDAO) dl.getDAO(User.class);

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String action = normalize(request.getParameter("action"));

            if ("delete".equals(action)) {
                eliminaStaff(request, response, userDAO);
            } else {
                salvaStaff(request, response, userDAO);
            }

            return;
        }

        mostraPagina(request, response, userDAO);
    }

    private void salvaStaff(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) throws Exception {
        String idParam = normalize(request.getParameter("id"));
        String nome = normalize(request.getParameter("nome"));
        String cognome = normalize(request.getParameter("cognome"));
        String email = normalize(request.getParameter("email"));
        String password = normalize(request.getParameter("password"));

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty()) {
            response.sendRedirect("admin-staff?error=campi");
            return;
        }

        User utenteConEmail = userDAO.getUserByEmail(email);

        if (utenteConEmail != null) {
            boolean stessaPersona = !idParam.isEmpty() && utenteConEmail.getKey().equals(Integer.parseInt(idParam));

            if (!stessaPersona) {
                response.sendRedirect("admin-staff?error=email");
                return;
            }
        }

        Staff staff;

        if (idParam.isEmpty()) {
            if (password.isEmpty()) {
                response.sendRedirect("admin-staff?error=password");
                return;
            }

            staff = new StaffImpl();
        } else {
            User utente = userDAO.getUserById(Integer.parseInt(idParam));

            if (!(utente instanceof Staff)) {
                response.sendRedirect("admin-staff?error=notfound");
                return;
            }

            staff = (Staff) utente;
        }

        staff.setName(nome);
        staff.setSurname(cognome);
        staff.setEmail(email);

        if (!password.isEmpty()) {
            staff.setPassword(password);
        }

        if (idParam.isEmpty()) {
            userDAO.addUser(staff);
            response.sendRedirect("admin-staff?success=add");
        } else {
            userDAO.updateUser(staff);
            response.sendRedirect("admin-staff?success=update");
        }
    }

    private void eliminaStaff(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) throws Exception {
        String idParam = normalize(request.getParameter("id"));

        if (idParam.isEmpty()) {
            response.sendRedirect("admin-staff?error=notfound");
            return;
        }

        User utente = userDAO.getUserById(Integer.parseInt(idParam));

        if (!(utente instanceof Staff)) {
            response.sendRedirect("admin-staff?error=notfound");
            return;
        }

        userDAO.deleteUser(utente);
        response.sendRedirect("admin-staff?success=delete");
    }

    private void mostraPagina(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) throws Exception {
        List<User> utenti = userDAO.getAllUsers();
        List<Staff> staffList = new ArrayList<>();

        for (User utente : utenti) {
            if (utente instanceof Staff) {
                staffList.add((Staff) utente);
            }
        }

        String editParam = normalize(request.getParameter("edit"));

        if (!editParam.isEmpty()) {
            User utente = userDAO.getUserById(Integer.parseInt(editParam));

            if (utente instanceof Staff) {
                request.setAttribute("staffEdit", utente);
            }
        }

        String success = normalize(request.getParameter("success"));
        String error = normalize(request.getParameter("error"));

        if ("add".equals(success)) {
            request.setAttribute("successo", "Account staff creato correttamente.");
        } else if ("update".equals(success)) {
            request.setAttribute("successo", "Account staff modificato correttamente.");
        } else if ("delete".equals(success)) {
            request.setAttribute("successo", "Account staff eliminato correttamente.");
        }

        if ("campi".equals(error)) {
            request.setAttribute("errore", "Compila nome, cognome ed email.");
        } else if ("password".equals(error)) {
            request.setAttribute("errore", "Inserisci una password per il nuovo account staff.");
        } else if ("email".equals(error)) {
            request.setAttribute("errore", "Esiste gia' un utente con questa email.");
        } else if ("notfound".equals(error)) {
            request.setAttribute("errore", "Account staff non trovato.");
        }

        request.setAttribute("staffList", staffList);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("admin-staff.ftl.html", request, response);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
