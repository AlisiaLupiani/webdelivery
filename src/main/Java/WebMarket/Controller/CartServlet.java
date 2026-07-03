package WebMarket.Controller;


import java.util.ArrayList;
import java.util.List;

import WebMarket.data.dao.CartDAO;
import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.ProductOptionDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartItem;
import model.Product;
import model.ProductOption;

@jakarta.servlet.annotation.WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends WebDeliveryBaseController {


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
        ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
        ProductOptionDAO optionDAO = (ProductOptionDAO) dl.getDAO(ProductOption.class);
      
        Cart carrello = cartDAO.getOrCreateActiveCart(userId);

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            int idProdotto = Integer.parseInt(request.getParameter("prodotto_id"));
            String[] idOpzioniScelte = request.getParameterValues("caratteristica_id");

            Product prodotto = productDAO.getProductById(idProdotto);

            if (prodotto != null) {
                double prezzoUnitario = prodotto.getPrice();
                List<Integer> opzioniDaSalvare = new ArrayList<>();

                if (idOpzioniScelte != null) {
                    for (String idOpzioneStr : idOpzioniScelte) {
                        int idOpzione = Integer.parseInt(idOpzioneStr);

                        ProductOption opzione = optionDAO.getProductOptionById(idOpzione);

                        if (opzione != null) {
                            prezzoUnitario += opzione.getAddictionalPrice();
                            opzioniDaSalvare.add(idOpzione);
                        }
                    }
                }

                CartItem nuovaRiga = cartItemDAO.addItem(
                        carrello.getKey(),
                        prodotto.getKey(),
                        1,
                        prezzoUnitario
                );

                if (nuovaRiga != null) {
                    for (Integer idOpzione : opzioniDaSalvare) {
                        cartItemDAO.addOptionToItem(nuovaRiga.getKey(), idOpzione);
                    }
                }
            }

            response.sendRedirect("cart");
            return;
        }

        if ("remove".equals(action)) {
            int cartItemId = Integer.parseInt(request.getParameter("cart_item_id"));
            cartItemDAO.removeItem(cartItemId);

            response.sendRedirect("cart");
            return;
        }

        if ("update".equals(action)) {
            int cartItemId = Integer.parseInt(request.getParameter("cart_item_id"));
            int quantita = Integer.parseInt(request.getParameter("quantita"));

            if (quantita <= 0) {
                cartItemDAO.removeItem(cartItemId);
            } else {
                cartItemDAO.updateQuantity(cartItemId, quantita);
            }

            response.sendRedirect("cart");
            return;
        }

        if ("clear".equals(action)) {
            cartItemDAO.clearCart(carrello.getKey());

            response.sendRedirect("cart");
            return;
        }

        List<CartItem> elementi = cartItemDAO.getItemsByCartId(carrello.getKey());
        carrello.setElementi(elementi);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        request.setAttribute("carrello", carrello);
        templateEngine.activate("cart.ftl.html", request, response);
    }
}