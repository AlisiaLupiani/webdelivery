package WebMarket.Controller;

import java.util.List;

import WebMarket.data.dao.IngredientDAO;
import WebMarket.data.dao.ProductDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Ingredient;
import model.Product;
import model.modelImpl.IngredientImpl;
import model.modelImpl.ProductImpl;

@WebServlet(name = "AdminProdottiServlet", urlPatterns = {"/admin-prodotti"})
public class AdminProdottiServlet extends WebDeliveryBaseController {

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
        ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
        IngredientDAO ingredientDAO = (IngredientDAO) dl.getDAO(Ingredient.class);

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String action = normalize(request.getParameter("action"));

            if ("delete".equals(action)) {
                eliminaProdotto(request, response, productDAO);
            } else if ("add_ingredient_catalog".equals(action)) {
                aggiungiIngredienteCatalogo(request, response, ingredientDAO);
            } else if ("add_product_ingredient".equals(action)) {
                aggiungiIngredienteProdotto(request, response, productDAO);
            } else if ("remove_product_ingredient".equals(action)) {
                rimuoviIngredienteProdotto(request, response, productDAO);
            } else {
                salvaProdotto(request, response, productDAO);
            }

            return;
        }

        mostraPagina(request, response, productDAO, ingredientDAO);
    }

    private void salvaProdotto(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO) throws Exception {
        String idParam = normalize(request.getParameter("id"));

        Product prodotto;

        if (idParam.isEmpty()) {
            prodotto = new ProductImpl();
        } else {
            prodotto = productDAO.getProductById(Integer.parseInt(idParam));
            if (prodotto == null) {
                response.sendRedirect("admin-prodotti?error=notfound");
                return;
            }
        }

        prodotto.setName(normalize(request.getParameter("nome")));
        prodotto.setDescription(normalize(request.getParameter("descrizione")));
        prodotto.setPrice(parseDouble(request.getParameter("prezzo")));
        prodotto.setProcedure(normalize(request.getParameter("procedura")));
        prodotto.setPreparationTime(parseInt(request.getParameter("tempo_preparazione")));
        prodotto.setImage(normalize(request.getParameter("immagine")));
        prodotto.setCategory(normalizeOrDefault(request.getParameter("categoria"), "Altro"));

        if (prodotto.getName().isEmpty()) {
            response.sendRedirect("admin-prodotti?error=campi");
            return;
        }

        if (idParam.isEmpty()) {
            productDAO.addProduct(prodotto);
            response.sendRedirect("admin-prodotti?edit=" + prodotto.getKey() + "&success=add");
        } else {
            productDAO.updateProduct(prodotto);
            response.sendRedirect("admin-prodotti?edit=" + prodotto.getKey() + "&success=update");
        }
    }

    private void eliminaProdotto(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO) throws Exception {
        String idParam = normalize(request.getParameter("id"));

        if (idParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=notfound");
            return;
        }

        Product prodotto = productDAO.getProductById(Integer.parseInt(idParam));

        if (prodotto == null) {
            response.sendRedirect("admin-prodotti?error=notfound");
            return;
        }

        productDAO.deleteProduct(prodotto);
        response.sendRedirect("admin-prodotti?success=delete");
    }

    private void aggiungiIngredienteCatalogo(HttpServletRequest request, HttpServletResponse response, IngredientDAO ingredientDAO) throws Exception {
        String nome = normalize(request.getParameter("nome_ingrediente"));

        if (nome.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=ingrediente");
            return;
        }

        Ingredient ingrediente = new IngredientImpl();
        ingrediente.setName(nome);

        ingredientDAO.addIngredient(ingrediente);
        response.sendRedirect("admin-prodotti?success=ingrediente");
    }

    private void aggiungiIngredienteProdotto(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO) throws Exception {
        String productIdParam = normalize(request.getParameter("product_id"));
        String ingredientIdParam = normalize(request.getParameter("ingredient_id"));
        String quantita = normalizeOrDefault(request.getParameter("quantita"), "q.b.");

        if (productIdParam.isEmpty() || ingredientIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=link");
            return;
        }

        int productId = Integer.parseInt(productIdParam);
        int ingredientId = Integer.parseInt(ingredientIdParam);

        productDAO.addIngredientToProduct(productId, ingredientId, quantita);
        response.sendRedirect("admin-prodotti?edit=" + productId + "&success=link");
    }

    private void rimuoviIngredienteProdotto(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO) throws Exception {
        String productIdParam = normalize(request.getParameter("product_id"));
        String ingredientIdParam = normalize(request.getParameter("ingredient_id"));

        if (productIdParam.isEmpty() || ingredientIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=link");
            return;
        }

        int productId = Integer.parseInt(productIdParam);
        int ingredientId = Integer.parseInt(ingredientIdParam);

        productDAO.removeIngredientFromProduct(productId, ingredientId);
        response.sendRedirect("admin-prodotti?edit=" + productId + "&success=unlink");
    }

    private void mostraPagina(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO, IngredientDAO ingredientDAO) throws Exception {
        List<Product> prodotti = productDAO.getAllProducts();
        List<Ingredient> ingredienti = ingredientDAO.getAllIngredients();

        String editParam = normalize(request.getParameter("edit"));
        if (!editParam.isEmpty()) {
            Product prodottoEdit = productDAO.getProductById(Integer.parseInt(editParam));
            request.setAttribute("prodottoEdit", prodottoEdit);

            if (prodottoEdit != null) {
                request.setAttribute("ingredientiProdotto", productDAO.getIngredientsByProductId(prodottoEdit.getKey()));
            }
        }

        String success = normalize(request.getParameter("success"));
        String error = normalize(request.getParameter("error"));

        if ("add".equals(success)) {
            request.setAttribute("successo", "Prodotto aggiunto correttamente. Ora puoi collegare gli ingredienti.");
        } else if ("update".equals(success)) {
            request.setAttribute("successo", "Prodotto modificato correttamente.");
        } else if ("delete".equals(success)) {
            request.setAttribute("successo", "Prodotto eliminato correttamente.");
        } else if ("ingrediente".equals(success)) {
            request.setAttribute("successo", "Ingrediente aggiunto correttamente.");
        } else if ("link".equals(success)) {
            request.setAttribute("successo", "Ingrediente collegato al prodotto.");
        } else if ("unlink".equals(success)) {
            request.setAttribute("successo", "Ingrediente rimosso dal prodotto.");
        }

        if ("campi".equals(error)) {
            request.setAttribute("errore", "Compila almeno il nome del prodotto.");
        } else if ("notfound".equals(error)) {
            request.setAttribute("errore", "Prodotto non trovato.");
        } else if ("ingrediente".equals(error)) {
            request.setAttribute("errore", "Inserisci il nome dell'ingrediente.");
        } else if ("link".equals(error)) {
            request.setAttribute("errore", "Seleziona un prodotto e un ingrediente validi.");
        }

        request.setAttribute("prodotti", prodotti);
        request.setAttribute("ingredienti", ingredienti);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("admin-prodotti.ftl.html", request, response);
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(normalize(value).replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(normalize(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private String normalizeOrDefault(String value, String defaultValue) {
        String normalized = normalize(value);
        return normalized.isEmpty() ? defaultValue : normalized;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}