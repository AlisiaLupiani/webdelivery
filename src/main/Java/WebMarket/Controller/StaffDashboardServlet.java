package WebMarket.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import WebMarket.data.dao.LogOrderStateDAO;
import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.UserDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.LogOrderState;
import model.Order;
import model.OrderState;
import model.Product;
import model.Staff;
import model.User;
import model.modelImpl.LogOrderStateImpl;

@WebServlet(name = "StaffDashboardServlet", urlPatterns = {"/staff"})
public class StaffDashboardServlet extends WebDeliveryBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login");
            return;
        }

        if (!checkRole(request, "STAFF")) {
            response.sendRedirect("home");
            return;
        }

        DataLayer dl = (DataLayer) request.getAttribute("datalayer");

        OrderDAO orderDAO = (OrderDAO) dl.getDAO(Order.class);
        ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
        UserDAO userDAO = (UserDAO) dl.getDAO(User.class);
        LogOrderStateDAO logDAO = (LogOrderStateDAO) dl.getDAO(LogOrderState.class);

        int staffId = Integer.parseInt(session.getAttribute("userid").toString());
        User user = userDAO.getUserById(staffId);

        if (!(user instanceof Staff)) {
            response.sendRedirect("home");
            return;
        }

        Staff staff = (Staff) user;

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            avanzaStatoOrdine(request, response, orderDAO, logDAO, staff);
            return;
        }

        mostraDashboard(request, response, orderDAO, productDAO);
    }

    private void avanzaStatoOrdine(
            HttpServletRequest request,
            HttpServletResponse response,
            OrderDAO orderDAO,
            LogOrderStateDAO logDAO,
            Staff staff) throws Exception {

        String ordineIdParam = request.getParameter("ordine_id");

        if (ordineIdParam == null || ordineIdParam.isBlank()) {
            response.sendRedirect("staff?error=ordine");
            return;
        }

        int ordineId = Integer.parseInt(ordineIdParam);
        Order ordine = orderDAO.getOrderById(ordineId);

        if (ordine == null) {
            response.sendRedirect("staff?error=ordine");
            return;
        }

        OrderState statoFrom = ordine.getOrderState();
        OrderState statoTo = getNextState(statoFrom);

        if (statoTo == null) {
            response.sendRedirect("staff?error=stato");
            return;
        }

        ordine.setOrderState(statoTo);
        orderDAO.updateOrder(ordine);

        LogOrderState log = new LogOrderStateImpl();
        log.setDateTime(LocalDateTime.now());
        log.setOrder(ordine);
        log.setStaff(staff);
        log.setStateFrom(statoFrom);
        log.setStateTo(statoTo);

        logDAO.addLogOrderState(log);

        response.sendRedirect("staff?success=stato");
    }

    private void mostraDashboard(
            HttpServletRequest request,
            HttpServletResponse response,
            OrderDAO orderDAO,
            ProductDAO productDAO) throws Exception {

        List<Order> ordini = orderDAO.getAllOrders();
        Map<String, List<Map<String, Object>>> prodottiPerOrdine = new HashMap<>();

        for (Order ordine : ordini) {
            prodottiPerOrdine.put(
                    String.valueOf(ordine.getKey()),
                    productDAO.getOrderProductDetails(ordine)
            );
        }

        String success = normalize(request.getParameter("success"));
        String error = normalize(request.getParameter("error"));

        if ("stato".equals(success)) {
            request.setAttribute("successo", "Stato ordine aggiornato correttamente.");
        }

        if ("ordine".equals(error)) {
            request.setAttribute("errore", "Ordine non valido.");
        } else if ("stato".equals(error)) {
            request.setAttribute("errore", "Questo ordine non puo' avanzare ulteriormente.");
        }

        request.setAttribute("ordini", ordini);
        request.setAttribute("prodottiPerOrdine", prodottiPerOrdine);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("staff-dashboard.ftl.html", request, response);
    }

    private OrderState getNextState(OrderState statoCorrente) {
        if (statoCorrente == null) {
            return OrderState.IN_PREPARAZIONE;
        }

        switch (statoCorrente) {
            case INSERITO:
                return OrderState.IN_PREPARAZIONE;
            case IN_PREPARAZIONE:
                return OrderState.PRONTO;
            case PRONTO:
                return OrderState.IN_CONSEGNA;
            case IN_CONSEGNA:
                return OrderState.CONSEGNATO;
            case CONSEGNATO:
                return null;
            default:
                return null;
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}