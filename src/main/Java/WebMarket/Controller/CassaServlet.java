package WebMarket.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import WebMarket.data.WebDeliveryDataLayer;
import WebMarket.data.dao.CartDAO;
import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.UserDAO;
import WebMarket.util.EmailService;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartItem;
import model.Client;
import model.Order;
import model.OrderState;
import model.PaymentMethod;
import model.Product;
import model.ProductOption;
import model.User;
import model.modelImpl.OrderImpl;

@WebServlet(name = "CassaServlet", urlPatterns = {"/cassa"})
public class CassaServlet extends WebDeliveryBaseController {

    private static final LocalTime ORARIO_CHIUSURA = LocalTime.of(23, 0);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = Integer.parseInt(session.getAttribute("userid").toString());
        WebDeliveryDataLayer dl = (WebDeliveryDataLayer) request.getAttribute("datalayer");

        CartDAO cartDAO = dl.getCartDAO();
        CartItemDAO cartItemDAO = dl.getCartItemDAO();
        UserDAO userDAO = dl.getUserDAO();
        OrderDAO orderDAO = dl.getOrderDAO();

        User utente = userDAO.getUserById(userId);

        if (!(utente instanceof Client)) {
            response.sendRedirect("login");
            return;
        }

        Client cliente = (Client) utente;

        Cart carrello = cartDAO.getActiveCartByUserId(userId);

        if (carrello == null) {
            response.sendRedirect("cart");
            return;
        }

        List<CartItem> elementi = cartItemDAO.getItemsByCartId(carrello.getKey());

        if (elementi == null || elementi.isEmpty()) {
            response.sendRedirect("cart");
            return;
        }

        carrello.setElementi(elementi);

        int tempoStimato = calcolaTempoStimato(elementi);
        LocalTime orarioMinimo = calcolaOrarioMinimo(tempoStimato);

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            if (!checkCsrf(request, response)) {
                return;
            }

            String action = request.getParameter("action");

            if ("annulla".equalsIgnoreCase(action)) {
                response.sendRedirect("cart");
                return;
            }

            String indirizzo = request.getParameter("indirizzo");
            String orarioParam = request.getParameter("orario_consegna");
            String metodoPagamentoParam = request.getParameter("metodo_pagamento");

            request.setAttribute("indirizzoInserito", indirizzo);
            request.setAttribute("orarioInserito", orarioParam);
            request.setAttribute("metodoPagamentoInserito", metodoPagamentoParam);

            if (indirizzo == null || indirizzo.isBlank() || indirizzo.trim().length() > 255) {
                request.setAttribute("erroreOrario", "Inserisci un indirizzo di consegna valido.");
                mostraCassa(request, response, cliente, carrello, tempoStimato, orarioMinimo);
                return;
            }

            LocalTime orarioConsegna;
            PaymentMethod metodoPagamento;

            try {
                orarioConsegna = LocalTime.parse(orarioParam);
            } catch (Exception ex) {
                request.setAttribute("erroreOrario", "Orario di consegna non valido.");
                mostraCassa(request, response, cliente, carrello, tempoStimato, orarioMinimo);
                return;
            }

            if (orarioMinimo.isAfter(ORARIO_CHIUSURA)) {
                request.setAttribute("erroreOrario", "Non e' piu possibile ordinare oggi: il tempo stimato supera l'orario di chiusura.");
                mostraCassa(request, response, cliente, carrello, tempoStimato, orarioMinimo);
                return;
            }

            if (orarioConsegna.isBefore(orarioMinimo)) {
                request.setAttribute("erroreOrario", "L'orario scelto e' troppo presto. Puoi scegliere dalle " + formatTime(orarioMinimo) + " in poi.");
                mostraCassa(request, response, cliente, carrello, tempoStimato, orarioMinimo);
                return;
            }

            if (orarioConsegna.isAfter(ORARIO_CHIUSURA)) {
                request.setAttribute("erroreOrario", "L'orario scelto supera l'orario di chiusura delle " + formatTime(ORARIO_CHIUSURA) + ".");
                mostraCassa(request, response, cliente, carrello, tempoStimato, orarioMinimo);
                return;
            }

            try {
                metodoPagamento = parsePaymentMethod(metodoPagamentoParam);
            } catch (IllegalArgumentException ex) {
                request.setAttribute("erroreOrario", "Metodo di pagamento non valido.");
                mostraCassa(request, response, cliente, carrello, tempoStimato, orarioMinimo);
                return;
            }

            Order ordine = new OrderImpl();

            ordine.setClient(cliente);
            ordine.setDate(LocalDate.now());
            ordine.setDeliveryTime(orarioConsegna);
            ordine.setPrice(carrello.getPrezzoTotaleCarrello());
            ordine.setDeliveryAddress(indirizzo.trim());
            ordine.setPaymentMethod(metodoPagamento);
            ordine.setOrderState(OrderState.INSERITO);

            try {
                dl.beginTransaction();
                orderDAO.addOrder(ordine);

                for (CartItem item : elementi) {
                    int orderProductId = orderDAO.addProductToOrder(ordine.getKey(), item);

                    if (item.getOpzioniScelte() != null) {
                        for (ProductOption opzione : item.getOpzioniScelte()) {
                            orderDAO.addOptionToOrderProduct(orderProductId, opzione);
                        }
                    }
                }

                cartDAO.closeCart(carrello.getKey());
                dl.commitTransaction();
            } catch (Exception ex) {
                dl.rollbackTransaction();
                throw ex;
            }

            try {
                EmailService.sendOrderConfirmation(
                        getServletContext(),
                        cliente,
                        ordine,
                        elementi,
                        tempoStimato
                );
            } catch (Exception ex) {
                getServletContext().log("Errore invio email conferma ordine", ex);
            }

            response.sendRedirect("ordine-confermato?id=" + ordine.getKey());
            return;
        }

        mostraCassa(request, response, cliente, carrello, tempoStimato, orarioMinimo);
    }

    private void mostraCassa(
            HttpServletRequest request,
            HttpServletResponse response,
            Client cliente,
            Cart carrello,
            int tempoStimato,
            LocalTime orarioMinimo) throws Exception {

        request.setAttribute("cliente", cliente);
        request.setAttribute("carrello", carrello);
        request.setAttribute("tempoStimato", tempoStimato);
        request.setAttribute("orarioMinimo", formatTime(orarioMinimo));
        request.setAttribute("orarioChiusura", formatTime(ORARIO_CHIUSURA));
        request.setAttribute("ordinePossibile", !orarioMinimo.isAfter(ORARIO_CHIUSURA));

        if (orarioMinimo.isAfter(ORARIO_CHIUSURA) && request.getAttribute("erroreOrario") == null) {
            request.setAttribute("erroreOrario", "Non e' piu possibile ordinare oggi: il tempo stimato supera l'orario di chiusura.");
        }

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("cassa.ftl.html", request, response);
    }

    private LocalTime calcolaOrarioMinimo(int tempoStimato) {
        LocalTime minimo = LocalTime.now().plusMinutes(tempoStimato);

        if (minimo.getSecond() > 0 || minimo.getNano() > 0) {
            minimo = minimo.plusMinutes(1);
        }

        return minimo.withSecond(0).withNano(0);
    }

    private String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    private int calcolaTempoStimato(List<CartItem> elementi) {
        int totaleMinuti = 0;

        for (CartItem item : elementi) {
            Product prodotto = item.getProdotto();

            if (prodotto != null) {
                totaleMinuti += prodotto.getPreparationTime() * item.getQuantita();
            }
        }

        return totaleMinuti;
    }

    private PaymentMethod parsePaymentMethod(String value) {
        PaymentMethod method = PaymentMethod.valueOf(value);

        if (method != PaymentMethod.CASH
                && method != PaymentMethod.VISA
                && method != PaymentMethod.MASTERCARD
                && method != PaymentMethod.PAYPAL) {
            throw new IllegalArgumentException("Metodo di pagamento non supportato");
        }

        return method;
    }
}
