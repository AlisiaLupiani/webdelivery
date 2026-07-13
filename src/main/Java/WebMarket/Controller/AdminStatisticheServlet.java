package WebMarket.Controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
import model.Client;
import model.Order;
import model.OrderState;
import model.PaymentMethod;
import model.Product;

@WebServlet(name = "AdminStatisticheServlet", urlPatterns = {"/admin-statistiche"})
public class AdminStatisticheServlet extends WebDeliveryBaseController {

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

        List<Order> ordini = orderDAO.getAllOrders();

        int ordiniAttivi = 0;
        int ordiniConsegnati = 0;
        double incassoTotale = 0.0;
        double incassoConsegnato = 0.0;

        Map<String, Map<String, Object>> ordiniPerStato = new LinkedHashMap<>();
        Map<String, Map<String, Object>> incassoPerGiorno = new LinkedHashMap<>();
        Map<String, Map<String, Object>> prodottiVenduti = new LinkedHashMap<>();
        Map<String, Map<String, Object>> categorie = new LinkedHashMap<>();
        Map<String, Map<String, Object>> clienti = new LinkedHashMap<>();
        Map<String, Map<String, Object>> pagamenti = new LinkedHashMap<>();

        for (OrderState stato : OrderState.values()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("stato", stato.name());
            row.put("quantita", 0);
            ordiniPerStato.put(stato.name(), row);
        }

        for (Order ordine : ordini) {
            double prezzoOrdine = ordine.getPrice() != null ? ordine.getPrice() : 0.0;
            incassoTotale += prezzoOrdine;

            OrderState stato = ordine.getOrderState();
            if (stato == OrderState.CONSEGNATO) {
                ordiniConsegnati++;
                incassoConsegnato += prezzoOrdine;
            } else {
                ordiniAttivi++;
            }

            if (stato != null && ordiniPerStato.containsKey(stato.name())) {
                Map<String, Object> row = ordiniPerStato.get(stato.name());
                row.put("quantita", getInt(row, "quantita") + 1);
            }

            String data = ordine.getDate() != null ? ordine.getDate().toString() : "Senza data";
            Map<String, Object> giornoRow = incassoPerGiorno.get(data);
            if (giornoRow == null) {
                giornoRow = new LinkedHashMap<>();
                giornoRow.put("data", data);
                giornoRow.put("ordini", 0);
                giornoRow.put("incasso", 0.0);
                incassoPerGiorno.put(data, giornoRow);
            }
            giornoRow.put("ordini", getInt(giornoRow, "ordini") + 1);
            giornoRow.put("incasso", getDouble(giornoRow, "incasso") + prezzoOrdine);

            PaymentMethod metodo = ordine.getPaymentMethod();
            String metodoPagamento = metodo != null ? metodo.name() : "NON_SPECIFICATO";
            Map<String, Object> pagamentoRow = pagamenti.get(metodoPagamento);
            if (pagamentoRow == null) {
                pagamentoRow = new LinkedHashMap<>();
                pagamentoRow.put("metodo", metodoPagamento);
                pagamentoRow.put("ordini", 0);
                pagamentoRow.put("incasso", 0.0);
                pagamenti.put(metodoPagamento, pagamentoRow);
            }
            pagamentoRow.put("ordini", getInt(pagamentoRow, "ordini") + 1);
            pagamentoRow.put("incasso", getDouble(pagamentoRow, "incasso") + prezzoOrdine);

            Client cliente = ordine.getClient();
            String clienteKey = cliente != null ? String.valueOf(cliente.getKey()) : "0";
            Map<String, Object> clienteRow = clienti.get(clienteKey);
            if (clienteRow == null) {
                clienteRow = new LinkedHashMap<>();
                clienteRow.put("nome", cliente != null ? cliente.getName() + " " + cliente.getSurname() : "Cliente non disponibile");
                clienteRow.put("email", cliente != null ? cliente.getEmail() : "");
                clienteRow.put("ordini", 0);
                clienteRow.put("speso", 0.0);
                clienti.put(clienteKey, clienteRow);
            }
            clienteRow.put("ordini", getInt(clienteRow, "ordini") + 1);
            clienteRow.put("speso", getDouble(clienteRow, "speso") + prezzoOrdine);

            List<Map<String, Object>> dettagliProdotti = productDAO.getOrderProductDetails(ordine);

            for (Map<String, Object> dettaglio : dettagliProdotti) {
                Product prodotto = (Product) dettaglio.get("prodotto");
                int quantita = getQuantity(dettaglio.get("quantita"));

                if (prodotto == null) {
                    continue;
                }

                double prezzoProdotto = prodotto.getPrice() != null ? prodotto.getPrice() : 0.0;
                double incassoProdotto = prezzoProdotto * quantita;

                String prodottoKey = String.valueOf(prodotto.getKey());
                Map<String, Object> prodottoRow = prodottiVenduti.get(prodottoKey);
                if (prodottoRow == null) {
                    prodottoRow = new LinkedHashMap<>();
                    prodottoRow.put("nome", prodotto.getName());
                    prodottoRow.put("categoria", prodotto.getCategory());
                    prodottoRow.put("quantita", 0);
                    prodottoRow.put("incasso", 0.0);
                    prodottiVenduti.put(prodottoKey, prodottoRow);
                }
                prodottoRow.put("quantita", getInt(prodottoRow, "quantita") + quantita);
                prodottoRow.put("incasso", getDouble(prodottoRow, "incasso") + incassoProdotto);

                String categoria = prodotto.getCategory() != null && !prodotto.getCategory().isBlank()
                        ? prodotto.getCategory()
                        : "Altro";

                Map<String, Object> categoriaRow = categorie.get(categoria);
                if (categoriaRow == null) {
                    categoriaRow = new LinkedHashMap<>();
                    categoriaRow.put("categoria", categoria);
                    categoriaRow.put("quantita", 0);
                    categoriaRow.put("incasso", 0.0);
                    categorie.put(categoria, categoriaRow);
                }
                categoriaRow.put("quantita", getInt(categoriaRow, "quantita") + quantita);
                categoriaRow.put("incasso", getDouble(categoriaRow, "incasso") + incassoProdotto);
            }
        }

        List<Map<String, Object>> prodottiRows = new ArrayList<>(prodottiVenduti.values());
        prodottiRows.sort(Comparator.comparingInt(row -> -getInt(row, "quantita")));

        List<Map<String, Object>> clientiRows = new ArrayList<>(clienti.values());
        clientiRows.sort(Comparator.comparingDouble(row -> -getDouble(row, "speso")));

        request.setAttribute("totaleOrdini", ordini.size());
        request.setAttribute("ordiniAttivi", ordiniAttivi);
        request.setAttribute("ordiniConsegnati", ordiniConsegnati);
        request.setAttribute("incassoTotale", incassoTotale);
        request.setAttribute("incassoConsegnato", incassoConsegnato);
        request.setAttribute("ordiniPerStato", new ArrayList<>(ordiniPerStato.values()));
        request.setAttribute("incassoPerGiorno", new ArrayList<>(incassoPerGiorno.values()));
        request.setAttribute("prodottiVenduti", prodottiRows);
        request.setAttribute("categorie", new ArrayList<>(categorie.values()));
        request.setAttribute("clienti", clientiRows);
        request.setAttribute("pagamenti", new ArrayList<>(pagamenti.values()));

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("admin-statistiche.ftl.html", request, response);
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

    private int getInt(Map<String, Object> row, String key) {
        Object value = row.get(key);

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    private double getDouble(Map<String, Object> row, String key) {
        Object value = row.get(key);

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        return 0.0;
    }
}