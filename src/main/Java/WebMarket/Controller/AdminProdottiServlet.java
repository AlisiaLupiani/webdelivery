package WebMarket.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import WebMarket.data.dao.IngredientDAO;
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.ProductOptionDAO;
import WebMarket.data.dao.ProductOptionGroupDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Ingredient;
import model.Product;
import model.ProductOption;
import model.ProductOptionGroup;
import model.modelImpl.IngredientImpl;
import model.modelImpl.ProductImpl;
import model.modelImpl.ProductOptionGroupImpl;
import model.modelImpl.ProductOptionImpl;

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
        ProductOptionDAO optionDAO = (ProductOptionDAO) dl.getDAO(ProductOption.class);
        ProductOptionGroupDAO groupDAO = (ProductOptionGroupDAO) dl.getDAO(ProductOptionGroup.class);

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            if (!checkCsrf(request, response)) {
                return;
            }

            String action = normalize(request.getParameter("action"));

            if ("delete".equals(action)) {
                eliminaProdotto(request, response, productDAO);
            } else if ("add_ingredient_catalog".equals(action)) {
                aggiungiIngredienteCatalogo(request, response, ingredientDAO);
            } else if ("add_product_ingredient".equals(action)) {
                aggiungiIngredienteProdotto(request, response, productDAO);
            } else if ("remove_product_ingredient".equals(action)) {
                rimuoviIngredienteProdotto(request, response, productDAO);
            } else if ("add_option_group".equals(action)) {
                aggiungiGruppoCaratteristica(request, response, groupDAO);
            } else if ("update_option_group".equals(action)) {
                modificaGruppoCaratteristica(request, response, groupDAO);
            } else if ("delete_option_group".equals(action)) {
                eliminaGruppoCaratteristica(request, response, groupDAO);
            } else if ("add_option".equals(action)) {
                aggiungiCaratteristica(request, response, optionDAO, groupDAO);
            } else if ("update_option".equals(action)) {
                modificaCaratteristica(request, response, optionDAO, groupDAO);
            } else if ("delete_option".equals(action)) {
                eliminaCaratteristica(request, response, optionDAO);
            } else if ("add_product_option".equals(action)) {
                collegaCaratteristicaProdotto(request, response, productDAO);
            } else if ("remove_product_option".equals(action)) {
                rimuoviCaratteristicaProdotto(request, response, productDAO);
            } else {
                salvaProdotto(request, response, productDAO);
            }

            return;
        }

        mostraPagina(request, response, productDAO, ingredientDAO, optionDAO, groupDAO);
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

    private void aggiungiGruppoCaratteristica(HttpServletRequest request, HttpServletResponse response, ProductOptionGroupDAO groupDAO) throws Exception {
        String nome = normalize(request.getParameter("nome_gruppo"));

        if (nome.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=gruppo");
            return;
        }

        ProductOptionGroup gruppo = new ProductOptionGroupImpl();
        gruppo.setName(nome);
        gruppo.setSingleChoice("on".equals(request.getParameter("scelta_singola")));

        groupDAO.addProductOptionGroup(gruppo);
        response.sendRedirect("admin-prodotti?success=gruppo");
    }

    private void modificaGruppoCaratteristica(HttpServletRequest request, HttpServletResponse response, ProductOptionGroupDAO groupDAO) throws Exception {
        String groupIdParam = normalize(request.getParameter("group_id"));
        String nome = normalize(request.getParameter("nome_gruppo"));

        if (groupIdParam.isEmpty() || nome.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=gruppo");
            return;
        }

        ProductOptionGroup gruppo = groupDAO.getProductOptionGroupById(Integer.parseInt(groupIdParam));

        if (gruppo == null) {
            response.sendRedirect("admin-prodotti?error=gruppo");
            return;
        }

        gruppo.setName(nome);
        gruppo.setSingleChoice("on".equals(request.getParameter("scelta_singola")));
        groupDAO.updateProductOptionGroup(gruppo);

        response.sendRedirect("admin-prodotti?success=gruppo_update");
    }

    private void eliminaGruppoCaratteristica(HttpServletRequest request, HttpServletResponse response, ProductOptionGroupDAO groupDAO) throws Exception {
        String groupIdParam = normalize(request.getParameter("group_id"));

        if (groupIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=gruppo");
            return;
        }

        ProductOptionGroup gruppo = groupDAO.getProductOptionGroupById(Integer.parseInt(groupIdParam));

        if (gruppo == null) {
            response.sendRedirect("admin-prodotti?error=gruppo");
            return;
        }

        groupDAO.deleteProductOptionGroup(gruppo);
        response.sendRedirect("admin-prodotti?success=gruppo_delete");
    }

    private void aggiungiCaratteristica(
            HttpServletRequest request,
            HttpServletResponse response,
            ProductOptionDAO optionDAO,
            ProductOptionGroupDAO groupDAO) throws Exception {

        String nome = normalize(request.getParameter("nome_caratteristica"));
        String groupIdParam = normalize(request.getParameter("group_id"));

        if (nome.isEmpty() || groupIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=caratteristica");
            return;
        }

        ProductOptionGroup gruppo = groupDAO.getProductOptionGroupById(Integer.parseInt(groupIdParam));

        if (gruppo == null) {
            response.sendRedirect("admin-prodotti?error=gruppo");
            return;
        }

        ProductOption opzione = new ProductOptionImpl();
        opzione.setName(nome);
        opzione.setDescription(normalize(request.getParameter("descrizione_caratteristica")));
        opzione.setAddictionalPrice(parseDouble(request.getParameter("prezzo_caratteristica")));
        opzione.setDefault("on".equals(request.getParameter("is_default")));
        opzione.setProductOptionGroup(gruppo);

        optionDAO.addProductOption(opzione);
        response.sendRedirect("admin-prodotti?success=caratteristica");
    }

    private void modificaCaratteristica(
            HttpServletRequest request,
            HttpServletResponse response,
            ProductOptionDAO optionDAO,
            ProductOptionGroupDAO groupDAO) throws Exception {

        String optionIdParam = normalize(request.getParameter("option_id"));
        String nome = normalize(request.getParameter("nome_caratteristica"));
        String groupIdParam = normalize(request.getParameter("group_id"));

        if (optionIdParam.isEmpty() || nome.isEmpty() || groupIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=caratteristica");
            return;
        }

        ProductOption opzione = optionDAO.getProductOptionById(Integer.parseInt(optionIdParam));
        ProductOptionGroup gruppo = groupDAO.getProductOptionGroupById(Integer.parseInt(groupIdParam));

        if (opzione == null || gruppo == null) {
            response.sendRedirect("admin-prodotti?error=caratteristica");
            return;
        }

        opzione.setName(nome);
        opzione.setDescription(normalize(request.getParameter("descrizione_caratteristica")));
        opzione.setAddictionalPrice(parseDouble(request.getParameter("prezzo_caratteristica")));
        opzione.setDefault("on".equals(request.getParameter("is_default")));
        opzione.setProductOptionGroup(gruppo);

        optionDAO.updateProductOption(opzione);
        response.sendRedirect("admin-prodotti?success=caratteristica_update");
    }

    private void eliminaCaratteristica(HttpServletRequest request, HttpServletResponse response, ProductOptionDAO optionDAO) throws Exception {
        String optionIdParam = normalize(request.getParameter("option_id"));

        if (optionIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=caratteristica");
            return;
        }

        ProductOption opzione = optionDAO.getProductOptionById(Integer.parseInt(optionIdParam));

        if (opzione == null) {
            response.sendRedirect("admin-prodotti?error=caratteristica");
            return;
        }

        optionDAO.deleteProductOption(opzione);
        response.sendRedirect("admin-prodotti?success=caratteristica_delete");
    }

    private void collegaCaratteristicaProdotto(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO) throws Exception {
        String productIdParam = normalize(request.getParameter("product_id"));
        String optionIdParam = normalize(request.getParameter("option_id"));

        if (productIdParam.isEmpty() || optionIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=product_option");
            return;
        }

        int productId = Integer.parseInt(productIdParam);
        int optionId = Integer.parseInt(optionIdParam);

        productDAO.addOptionToProduct(productId, optionId);
        response.sendRedirect("admin-prodotti?edit=" + productId + "&success=product_option");
    }

    private void rimuoviCaratteristicaProdotto(HttpServletRequest request, HttpServletResponse response, ProductDAO productDAO) throws Exception {
        String productIdParam = normalize(request.getParameter("product_id"));
        String optionIdParam = normalize(request.getParameter("option_id"));

        if (productIdParam.isEmpty() || optionIdParam.isEmpty()) {
            response.sendRedirect("admin-prodotti?error=product_option");
            return;
        }

        int productId = Integer.parseInt(productIdParam);
        int optionId = Integer.parseInt(optionIdParam);

        productDAO.removeOptionFromProduct(productId, optionId);
        response.sendRedirect("admin-prodotti?edit=" + productId + "&success=product_option_remove");
    }

    private void mostraPagina(
            HttpServletRequest request,
            HttpServletResponse response,
            ProductDAO productDAO,
            IngredientDAO ingredientDAO,
            ProductOptionDAO optionDAO,
            ProductOptionGroupDAO groupDAO) throws Exception {

        List<Product> prodotti = productDAO.getAllProducts();
        List<Ingredient> ingredienti = ingredientDAO.getAllIngredients();
        List<ProductOptionGroup> gruppi = groupDAO.getAllProductOptionGroups();
        List<Map<String, Object>> opzioniCatalogo = buildOpzioniCatalogo(optionDAO, gruppi);

        String editParam = normalize(request.getParameter("edit"));
        if (!editParam.isEmpty()) {
            Product prodottoEdit = productDAO.getProductById(Integer.parseInt(editParam));
            request.setAttribute("prodottoEdit", prodottoEdit);

            if (prodottoEdit != null) {
                request.setAttribute("ingredientiProdotto", productDAO.getIngredientsByProductId(prodottoEdit.getKey()));
                request.setAttribute("caratteristicheProdotto", productDAO.getOptionsByProductId(prodottoEdit.getKey()));
            }
        }

        setMessages(request);

        request.setAttribute("prodotti", prodotti);
        request.setAttribute("ingredienti", ingredienti);
        request.setAttribute("gruppiCaratteristiche", gruppi);
        request.setAttribute("opzioniCatalogo", opzioniCatalogo);

        TemplateResult templateEngine = new TemplateResult(getServletContext());
        templateEngine.activate("admin-prodotti.ftl.html", request, response);
    }

    private List<Map<String, Object>> buildOpzioniCatalogo(ProductOptionDAO optionDAO, List<ProductOptionGroup> gruppi) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();

        for (ProductOptionGroup gruppo : gruppi) {
            List<ProductOption> opzioni = optionDAO.getProductOptionsByProductOptionGroup(gruppo);

            for (ProductOption opzione : opzioni) {
                Map<String, Object> row = new HashMap<>();
                row.put("opzione", opzione);
                row.put("gruppo", gruppo);
                result.add(row);
            }
        }

        return result;
    }

    private void setMessages(HttpServletRequest request) {
        String success = normalize(request.getParameter("success"));
        String error = normalize(request.getParameter("error"));

        if ("add".equals(success)) {
            request.setAttribute("successo", "Prodotto aggiunto correttamente. Ora puoi collegare ingredienti e caratteristiche.");
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
        } else if ("gruppo".equals(success)) {
            request.setAttribute("successo", "Gruppo caratteristica aggiunto correttamente.");
        } else if ("gruppo_update".equals(success)) {
            request.setAttribute("successo", "Gruppo caratteristica modificato correttamente.");
        } else if ("gruppo_delete".equals(success)) {
            request.setAttribute("successo", "Gruppo caratteristica eliminato correttamente.");
        } else if ("caratteristica".equals(success)) {
            request.setAttribute("successo", "Caratteristica aggiunta correttamente.");
        } else if ("caratteristica_update".equals(success)) {
            request.setAttribute("successo", "Caratteristica modificata correttamente.");
        } else if ("caratteristica_delete".equals(success)) {
            request.setAttribute("successo", "Caratteristica eliminata correttamente.");
        } else if ("product_option".equals(success)) {
            request.setAttribute("successo", "Caratteristica collegata al prodotto.");
        } else if ("product_option_remove".equals(success)) {
            request.setAttribute("successo", "Caratteristica rimossa dal prodotto.");
        }

        if ("campi".equals(error)) {
            request.setAttribute("errore", "Compila almeno il nome del prodotto.");
        } else if ("notfound".equals(error)) {
            request.setAttribute("errore", "Prodotto non trovato.");
        } else if ("ingrediente".equals(error)) {
            request.setAttribute("errore", "Inserisci il nome dell'ingrediente.");
        } else if ("link".equals(error)) {
            request.setAttribute("errore", "Seleziona un prodotto e un ingrediente validi.");
        } else if ("gruppo".equals(error)) {
            request.setAttribute("errore", "Controlla i dati del gruppo caratteristica.");
        } else if ("caratteristica".equals(error)) {
            request.setAttribute("errore", "Controlla i dati della caratteristica.");
        } else if ("product_option".equals(error)) {
            request.setAttribute("errore", "Seleziona un prodotto e una caratteristica validi.");
        }
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
