package WebMarket.Controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import WebMarket.data.dao.LogOrderStateDAO;
import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.ProductDAO;
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

@WebServlet(name = "AdminOrdiniServlet", urlPatterns = {"/admin-ordini"})
public class AdminOrdiniServlet extends WebDeliveryBaseController {

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
        LogOrderStateDAO logDAO = (LogOrderStateDAO) dl.getDAO(LogOrderState.class);

        List<Order> ordini = orderDAO.getAllOrders();

        Map<String, Integer> ordiniPerStato = new LinkedHashMap<>();
        Map<String, List<Order>> ordiniPerStatoDettagli = new LinkedHashMap<>();
        Map<String, List<Map<String, Object>>> prodottiPerOrdine = new LinkedHashMap<>();
        Map<String, List<LogOrderState>> logPerOrdine = new LinkedHashMap<>();
        Map<String, Integer> numeroProdottiPerOrdine = new LinkedHashMap<>();
        Map<String, Integer> tempoPreparazionePerOrdine = new LinkedHashMap<>();

        for (OrderState stato : OrderState.values()) {
            ordiniPerStato.put(stato.name(), 0);
            ordiniPerStatoDettagli.put(stato.name(), new ArrayList<>());
        }

        for (Order ordine : ordini) {
            String ordineKey = String.valueOf(ordine.getKey());
            OrderState stato = ordine.getOrderState();

            if (stato != null) {
                ordiniPerStato.put(stato.name(), ordiniPerStato.get(stato.name()) + 1);
                ordiniPerStatoDettagli.get(stato.name()).add(ordine);
            }

            List<Map<String, Object>> dettagliProdotti = productDAO.getOrderProductDetails(ordine);
            prodottiPerOrdine.put(ordineKey, dettagliProdotti);

            int numeroProdotti = 0;
            int tempoPreparazione = 0;

            for (Map<String, Object> dettaglio : dettagliProdotti) {
                int quantita = getQuantity(dettaglio.get("quantita"));
                numeroProdotti += quantita;

                Product prodotto = (Product) dettaglio.get("prodotto");
                if (prodotto != null && prodotto.getPreparationTime() != null) {
                    tempoPreparazione += prodotto.getPreparationTime() * quantita;
                }
            }

            numeroProdottiPerOrdine.put(ordineKey, numeroProdotti);
            tempoPreparazionePerOrdine.put(ordineKey, tempoPreparazione);

            List<LogOrderState> logs = logDAO.getLogOrderStateByOrder(ordine);
            logs.sort(Comparator.comparing(
                    LogOrderState::getDateTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ));
            logPerOrdine.put(ordineKey, logs);
        }

        request.setAttribute("ordiniPerStato", ordiniPerStato);
        request.setAttribute("ordiniPerStatoDettagli", ordiniPerStatoDettagli);
        request.setAttribute("prodottiPerOrdine", prodottiPerOrdine);
        request.setAttribute("logPerOrdine", logPerOrdine);
        request.setAttribute("numeroProdottiPerOrdine", numeroProdottiPerOrdine);
        request.setAttribute("tempoPreparazionePerOrdine", tempoPreparazionePerOrdine);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("admin-ordini.ftl.html", request, response);
    }

    private int getQuantity(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }
}