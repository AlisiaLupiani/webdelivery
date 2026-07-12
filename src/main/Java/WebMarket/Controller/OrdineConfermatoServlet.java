package WebMarket.Controller;

import WebMarket.data.dao.OrderDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Order;

@WebServlet(name = "OrdineConfermatoServlet", urlPatterns = {"/ordine-confermato"})
public class OrdineConfermatoServlet extends WebDeliveryBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login");
            return;
        }

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect("home");
            return;
        }

        int ordineId = Integer.parseInt(idParam);

        DataLayer dl = (DataLayer) request.getAttribute("datalayer");
        OrderDAO orderDAO = (OrderDAO) dl.getDAO(Order.class);

        Order ordine = orderDAO.getOrderById(ordineId);

        if (ordine == null) {
            response.sendRedirect("home");
            return;
        }

        request.setAttribute("ordine", ordine);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("ordine-confermato.ftl.html", request, response);
    }
}