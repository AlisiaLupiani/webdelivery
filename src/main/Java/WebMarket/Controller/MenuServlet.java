package WebMarket.Controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import WebMarket.data.dao.CartDAO;
import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.dao.ProductDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartItem;
import model.Product;

@jakarta.servlet.annotation.WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends WebDeliveryBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            DataLayer dl = (DataLayer) request.getAttribute("datalayer");

            ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
            CartDAO cartDAO = (CartDAO) dl.getDAO(Cart.class);
            CartItemDAO cartItemDAO = (CartItemDAO) dl.getDAO(CartItem.class);

            List<Product> listaProdotti = productDAO.getAllProducts();

            Map<String, List<Product>> prodottiPerCategoria = new LinkedHashMap<>();

            for (Product prodotto : listaProdotti) {
                String categoria = prodotto.getCategory();

                if (categoria == null || categoria.isBlank()) {
                    categoria = "Altro";
                }

                prodottiPerCategoria
                        .computeIfAbsent(categoria, k -> new ArrayList<>())
                        .add(prodotto);
            }

            request.setAttribute("prodottiPerCategoria", prodottiPerCategoria);

            HttpSession session = request.getSession(false);

            if (session != null && session.getAttribute("userid") != null) {
                int userId = Integer.parseInt(session.getAttribute("userid").toString());

                Cart carrello = cartDAO.getOrCreateActiveCart(userId);
                List<CartItem> elementi = cartItemDAO.getItemsByCartId(carrello.getKey());
                carrello.setElementi(elementi);

                request.setAttribute("carrello", carrello);
            }

            TemplateResult templateEngine = new TemplateResult(getServletContext());
            templateEngine.activate("menu.ftl.html", request, response);

        } catch (Exception ex) {
            handleError(ex, request, response);
        }
    }
}