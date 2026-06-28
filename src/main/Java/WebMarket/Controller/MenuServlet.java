package WebMarket.Controller;

import java.util.List;

import javax.sql.DataSource;

import WebMarket.data.dao.CartDAO;
import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.dao.ProductDAO;

import WebMarket.data.daoimpl.CartDAOImpl;
import WebMarket.data.daoimpl.CartItemDAOImpl;
import WebMarket.data.daoimpl.ProductDAOImpl;

import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import framework.view.TemplateResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Cart;
import model.CartItem;
import model.Product;

@jakarta.servlet.annotation.WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends AbstractBaseController {

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            DataLayer dl = new framework.data.DataLayer(ds);

            dl.registerDAO(model.Product.class, new ProductDAOImpl(dl));
            dl.registerDAO(model.Cart.class, new CartDAOImpl(dl));
            dl.registerDAO(model.CartItem.class, new CartItemDAOImpl(dl));

            return dl;

        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            DataLayer dl = (DataLayer) request.getAttribute("datalayer");

            ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
            CartDAO cartDAO = (CartDAO) dl.getDAO(Cart.class);
            CartItemDAO cartItemDAO = (CartItemDAO) dl.getDAO(CartItem.class);

            List<Product> listaProdotti = productDAO.getAllProducts();
            request.setAttribute("prodotti", listaProdotti);

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