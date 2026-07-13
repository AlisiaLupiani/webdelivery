package WebMarket.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import WebMarket.data.dao.CartDAO;
import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.dao.OrderDAO;
import WebMarket.data.dao.UserDAO;
import WebMarket.util.EmailService;
import framework.data.DataLayer;
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = Integer.parseInt(session.getAttribute("userid").toString());
        DataLayer dl = (DataLayer) request.getAttribute("datalayer");

        CartDAO cartDAO = (CartDAO) dl.getDAO(Cart.class);
        CartItemDAO cartItemDAO = (CartItemDAO) dl.getDAO(CartItem.class);
        UserDAO userDAO = (UserDAO) dl.getDAO(User.class);
        OrderDAO orderDAO = (OrderDAO) dl.getDAO(Order.class);

        User utente = userDAO.getUserById(userId);

        if (!(utente instanceof Client)) {
            response.sendRedirect("login");
            return;
        }

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

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Order ordine = new OrderImpl();

            ordine.setClient((Client) utente);
            ordine.setDate(LocalDate.now());
            ordine.setDeliveryTime(LocalTime.parse(request.getParameter("orario_consegna")));
            ordine.setPrice(carrello.getPrezzoTotaleCarrello());
            ordine.setDeliveryAddress(request.getParameter("indirizzo"));
            ordine.setPaymentMethod(PaymentMethod.valueOf(request.getParameter("metodo_pagamento")));
            ordine.setOrderState(OrderState.INSERITO);

            orderDAO.addOrder(ordine);

            for (CartItem item : elementi) {
                orderDAO.addProductToOrder(ordine.getKey(), item.getProductId(), item.getQuantita());

                if (item.getOpzioniScelte() != null) {
                    for (ProductOption opzione : item.getOpzioniScelte()) {
                        orderDAO.addOptionToOrderProduct(
                                ordine.getKey(),
                                item.getProductId(),
                                opzione.getKey()
                        );
                    }
                }
            }

            try {
                EmailService.sendOrderConfirmation(
                        getServletContext(),
                        (Client) utente,
                        ordine,
                        elementi,
                        tempoStimato
                );
            } catch (Exception ex) {
                getServletContext().log("Errore invio email conferma ordine", ex);
            }

            cartDAO.closeCart(carrello.getKey());

            response.sendRedirect("ordine-confermato?id=" + ordine.getKey());
            return;
        }

        request.setAttribute("cliente", utente);
        request.setAttribute("carrello", carrello);
        request.setAttribute("tempoStimato", tempoStimato);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("cassa.ftl.html", request, response);
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
}