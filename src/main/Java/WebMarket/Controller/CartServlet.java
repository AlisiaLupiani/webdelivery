package WebMarket.Controller;

import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import framework.view.TemplateResult;

// IMPORT DEI TUOI DAO
import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.ProductOptionDAO;
import WebMarket.data.daoimpl.ProductDAOImpl;
import WebMarket.data.daoimpl.ProductOptionDAOImpl;

// IMPORT DEI MODELLI
import model.Product;
import model.ProductOption;
import model.Cart;
import model.CartItem;
import model.modelImpl.CartImpl;
import model.modelImpl.CartItemImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;

@jakarta.servlet.annotation.WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends AbstractBaseController {

    // 1. RIEMPIAMO LA CASSETTA DEGLI ATTREZZI (Esattamente come in DettaglioServlet)
    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            DataLayer dl = new framework.data.DataLayer(ds);
            
            // Registriamo i DAO che ci servono per il carrello
            dl.registerDAO(model.Product.class, new ProductDAOImpl(dl));
            dl.registerDAO(model.ProductOption.class, new ProductOptionDAOImpl(dl));
            
            return dl;
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    // 2. IL CUORE DELLA SERVLET
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Recuperiamo la Sessione dell'utente
        HttpSession session = request.getSession();
        
        // Controlliamo se ha già un carrello. Se no, lo creiamo
        Cart carrello = (Cart) session.getAttribute("carrello_utente");
        if (carrello == null) {
            carrello = new CartImpl();
            session.setAttribute("carrello_utente", carrello);
        }

        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            int idProdotto = Integer.parseInt(request.getParameter("prodotto_id"));
            String[] idOpzioniScelte = request.getParameterValues("caratteristica_id");

            // Apriamo la cassetta degli attrezzi
            DataLayer dl = (DataLayer) request.getAttribute("datalayer");
            
            // Usiamo il tuo metodo corretto per estrarre i DAO
            ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
            ProductOptionDAO optionDAO = (ProductOptionDAO) dl.getDAO(ProductOption.class);
            
            // Estraiamo il prodotto usando il metodo esatto che hai nel Dettaglio!
            Product prodotto = productDAO.getProductById(idProdotto);
            
            if (prodotto != null) {
                CartItem nuovaRiga = new CartItemImpl();
                nuovaRiga.setProdotto(prodotto);
                nuovaRiga.setQuantita(1); 

                // Aggiungiamo le eventuali opzioni selezionate
                if (idOpzioniScelte != null) {
                    for (String idOpzioneStr : idOpzioniScelte) {
                        int idOpzione = Integer.parseInt(idOpzioneStr);
                        
                        // NOTA BENE: Ho supposto che il metodo per prendere una singola opzione
                        // si chiami getProductOptionById(). Se nel tuo DAO si chiama diversamente 
                        // (es. getOptionById), correggilo solo in questa riga qui sotto!
                        ProductOption opzione = optionDAO.getProductOptionById(idOpzione);
                        
                        if (opzione != null) {
                            nuovaRiga.addOpzione(opzione);
                        }
                    }
                }
                
                carrello.addItem(nuovaRiga);
            }
            
            // Rimandiamo alla pagina del carrello senza ri-eseguire l'aggiunta
            response.sendRedirect("cart");
            return;
        }

        // Se non stiamo aggiungendo nulla, mostriamo la pagina del carrello HTML
        TemplateResult templateEngine = new TemplateResult(getServletContext());
        request.setAttribute("carrello", carrello); 
        templateEngine.activate("cart.ftl.html", request, response);
    }
}