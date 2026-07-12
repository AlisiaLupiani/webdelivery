package WebMarket.Controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Client;
import model.Order;
import model.OrderState;
import model.Product;
import model.Proprietor;
import model.Staff;
import model.User;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin"})
public class AdminDashboardServlet extends WebDeliveryBaseController {

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

        OrderDAO orderDAO = (OrderDAO) dl.getDAO(Order.class);
        ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
        UserDAO userDAO = (UserDAO) dl.getDAO(User.class);

        List<Order> ordini = orderDAO.getAllOrders();
        List<Product> prodotti = productDAO.getAllProducts();
        List<User> utenti = userDAO.getAllUsers();

        int ordiniAttivi = 0;
        int ordiniConsegnati = 0;
        double incassoTotale = 0.0;
        double incassoConsegnato = 0.0;

        Map<String, Integer> ordiniPerStato = new LinkedHashMap<>();
        for (OrderState stato : OrderState.values()) {
            ordiniPerStato.put(stato.name(), 0);
        }

        for (Order ordine : ordini) {
            OrderState stato = ordine.getOrderState();

            if (stato != null) {
                ordiniPerStato.put(stato.name(), ordiniPerStato.get(stato.name()) + 1);
            }

            double prezzo = ordine.getPrice() != null ? ordine.getPrice() : 0.0;
            incassoTotale += prezzo;

            if (stato == OrderState.CONSEGNATO) {
                ordiniConsegnati++;
                incassoConsegnato += prezzo;
            } else {
                ordiniAttivi++;
            }
        }

        int totaleClienti = 0;
        int totaleStaff = 0;
        int totaleAdmin = 0;

        for (User utente : utenti) {
            if (utente instanceof Proprietor) {
                totaleAdmin++;
            } else if (utente instanceof Staff) {
                totaleStaff++;
            } else if (utente instanceof Client) {
                totaleClienti++;
            }
        }

        List<Order> ultimiOrdini = new ArrayList<>();
        for (int i = 0; i < ordini.size() && i < 8; i++) {
            ultimiOrdini.add(ordini.get(i));
        }

        request.setAttribute("totaleOrdini", ordini.size());
        request.setAttribute("ordiniAttivi", ordiniAttivi);
        request.setAttribute("ordiniConsegnati", ordiniConsegnati);
        request.setAttribute("incassoTotale", incassoTotale);
        request.setAttribute("incassoConsegnato", incassoConsegnato);
        request.setAttribute("totaleProdotti", prodotti.size());
        request.setAttribute("totaleUtenti", utenti.size());
        request.setAttribute("totaleClienti", totaleClienti);
        request.setAttribute("totaleStaff", totaleStaff);
        request.setAttribute("totaleAdmin", totaleAdmin);
        request.setAttribute("ordiniPerStato", ordiniPerStato);
        request.setAttribute("ultimiOrdini", ultimiOrdini);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("admin-dashboard.ftl.html", request, response);
    }
}