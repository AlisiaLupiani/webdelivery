package WebMarket.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import WebMarket.data.WebDeliveryDataLayer;
import WebMarket.data.dao.CartDAO;
import WebMarket.data.dao.CartItemDAO;
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.ProductOptionDAO;
import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.CartItem;
import model.Product;
import model.ProductOption;
import model.ProductOptionGroup;

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

        if ("POST".equalsIgnoreCase(request.getMethod()) && !checkCsrf(request, response)) {
            return;
        }

        WebDeliveryDataLayer dl = (WebDeliveryDataLayer) request.getAttribute("datalayer");

        CartDAO cartDAO = dl.getCartDAO();
        CartItemDAO cartItemDAO = dl.getCartItemDAO();
        ProductDAO productDAO = dl.getProductDAO();
        ProductOptionDAO optionDAO = dl.getProductOptionDAO();

        Cart carrello = cartDAO.getOrCreateActiveCart(userId);

        String action = request.getParameter("action");

        if (action != null && !action.isBlank() && !"POST".equalsIgnoreCase(request.getMethod())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        if ("add".equals(action)) {
            int idProdotto = parsePositiveInt(request.getParameter("prodotto_id"));

            if (idProdotto <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Prodotto non valido.");
                return;
            }

            Product prodotto = productDAO.getProductById(idProdotto);

            if (prodotto == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            try {
                List<ProductOption> opzioniDaSalvare =
                        validateSelectedOptions(request, optionDAO, prodotto.getKey());
                double prezzoUnitario = prodotto.getPrice();

                for (ProductOption opzione : opzioniDaSalvare) {
                    prezzoUnitario += opzione.getAddictionalPrice();
                }

                dl.beginTransaction();
                CartItem nuovaRiga = cartItemDAO.addItem(
                        carrello.getKey(),
                        prodotto.getKey(),
                        1,
                        prezzoUnitario
                );

                if (nuovaRiga == null) {
                    throw new IllegalStateException("Impossibile aggiungere il prodotto al carrello.");
                }

                for (ProductOption opzione : opzioniDaSalvare) {
                    cartItemDAO.addOptionToItem(nuovaRiga.getKey(), opzione.getKey());
                }
                dl.commitTransaction();
            } catch (IllegalArgumentException ex) {
                dl.rollbackTransaction();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return;
            } catch (Exception ex) {
                dl.rollbackTransaction();
                throw ex;
            }

            response.sendRedirect("cart");
            return;
        }

        if ("remove".equals(action)) {
            int cartItemId = parsePositiveInt(request.getParameter("cart_item_id"));

            if (cartItemId <= 0 || !cartItemDAO.removeItem(cartItemId, carrello.getKey())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.sendRedirect("cart");
            return;
        }

        if ("update".equals(action)) {
            int cartItemId = parsePositiveInt(request.getParameter("cart_item_id"));
            int quantita = parsePositiveInt(request.getParameter("quantita"));

            if (cartItemId <= 0 || quantita < 0 || quantita > 99) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quantita' non valida.");
                return;
            }

            if (quantita <= 0) {
                if (!cartItemDAO.removeItem(cartItemId, carrello.getKey())) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } else {
                if (!cartItemDAO.updateQuantity(cartItemId, carrello.getKey(), quantita)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
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

        request.setAttribute("carrello", carrello);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("cart.ftl.html", request, response);
    }

    private List<ProductOption> validateSelectedOptions(
            HttpServletRequest request,
            ProductOptionDAO optionDAO,
            int productId) throws Exception {

        Set<Integer> requestedIds = getSelectedOptionIds(request);
        List<ProductOption> availableOptions = optionDAO.getProductOptionsByProduct(productId);
        Map<Integer, ProductOption> availableById = new LinkedHashMap<>();

        for (ProductOption option : availableOptions) {
            availableById.put(option.getKey(), option);
        }

        List<ProductOption> selectedOptions = new ArrayList<>();
        Map<Integer, Integer> selectionsByGroup = new HashMap<>();

        for (Integer optionId : requestedIds) {
            ProductOption option = availableById.get(optionId);

            if (option == null) {
                throw new IllegalArgumentException("Una caratteristica non appartiene al prodotto selezionato.");
            }

            ProductOptionGroup group = option.getProductOptionGroup();
            if (group == null) {
                throw new IllegalArgumentException("Gruppo caratteristica non valido.");
            }

            int selectedCount = selectionsByGroup.getOrDefault(group.getKey(), 0) + 1;
            if (group.isSingleChoice() && selectedCount > 1) {
                throw new IllegalArgumentException("Puoi scegliere una sola caratteristica per gruppo.");
            }

            selectionsByGroup.put(group.getKey(), selectedCount);
            selectedOptions.add(option);
        }

        for (ProductOption option : availableOptions) {
            ProductOptionGroup group = option.getProductOptionGroup();

            if (group != null && group.isSingleChoice()
                    && !selectionsByGroup.containsKey(group.getKey())
                    && option.isDefault()) {
                selectionsByGroup.put(group.getKey(), 1);
                selectedOptions.add(option);
            }
        }

        return selectedOptions;
    }

    private Set<Integer> getSelectedOptionIds(HttpServletRequest request) {
        Set<Integer> result = new LinkedHashSet<>();

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (!entry.getKey().startsWith("caratteristica_id_")) {
                continue;
            }

            String[] values = entry.getValue();
            if (values == null) {
                continue;
            }

            for (String value : values) {
                try {
                    result.add(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                    // parametro non valido: lo ignoriamo
                }
            }
        }

        return result;
    }

    private int parsePositiveInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return -1;
        }
    }
}
