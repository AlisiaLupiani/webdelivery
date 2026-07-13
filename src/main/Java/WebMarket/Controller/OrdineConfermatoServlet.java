package WebMarket.Controller;

import java.util.List;
import java.util.Map;

import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.ProductDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Order;
import model.Product;

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
        ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);

        Order ordine = orderDAO.getOrderById(ordineId);

        if (ordine == null) {
            response.sendRedirect("home");
            return;
        }

        List<Map<String, Object>> prodottiOrdine = productDAO.getOrderProductDetails(ordine);

        request.setAttribute("ordine", ordine);
        request.setAttribute("prodottiOrdine", prodottiOrdine);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("ordine-confermato.ftl.html", request, response);
    }
}