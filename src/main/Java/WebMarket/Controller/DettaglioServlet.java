package WebMarket.Controller;

import framework.controller.AbstractBaseController;
import framework.data.DataLayer;
import framework.view.TemplateResult;

import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.ProductOptionGroupDAO;
import WebMarket.data.dao.ProductOptionDAO;
import WebMarket.data.daoimpl.ProductDAOImpl;
import WebMarket.data.daoimpl.ProductOptionGroupDAOImpl;
import WebMarket.data.daoimpl.ProductOptionDAOImpl;

import model.Product;
import model.ProductOptionGroup;
import model.ProductOption;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import javax.sql.DataSource;

@jakarta.servlet.annotation.WebServlet(name = "DettaglioServlet", urlPatterns = {"/dettaglio"})
public class DettaglioServlet extends AbstractBaseController {

    // 1. RIEMPIAMO LA CASSETTA DEGLI ATTREZZI CON I 3 DAO NECESSARI
    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            DataLayer dl = new framework.data.DataLayer(ds);
            
            // Attrezzo per il prodotto
            dl.registerDAO(model.Product.class, new ProductDAOImpl(dl));
            // Attrezzo per i gruppi (es. Salse, Cottura)
            dl.registerDAO(model.ProductOptionGroup.class, new ProductOptionGroupDAOImpl(dl));
            // Attrezzo per le singole opzioni (es. Ketchup, Ben Cotta)
            dl.registerDAO(model.ProductOption.class, new ProductOptionDAOImpl(dl));
            
            return dl;
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    // 2. IL CUORE DELLA SERVLET
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // Prendiamo l'ID cliccato dall'utente
            int idProdotto = Integer.parseInt(request.getParameter("id"));
            
            // Apriamo la cassetta degli attrezzi
            DataLayer dl = (DataLayer) request.getAttribute("datalayer");
            
            ProductDAO productDAO = (ProductDAO) dl.getDAO(Product.class);
            ProductOptionGroupDAO groupDAO = (ProductOptionGroupDAO) dl.getDAO(ProductOptionGroup.class);
            ProductOptionDAO optionDAO = (ProductOptionDAO) dl.getDAO(ProductOption.class);
            
            // Estraiamo il prodotto
            Product prodotto = productDAO.getProductById(idProdotto);
            
            // Estraiamo i gruppi collegati a questo specifico prodotto
            List<ProductOptionGroup> gruppi = groupDAO.getProductOptionGroupsByProduct(prodotto);
            
            // Riempiamo ogni gruppo con le sue rispettive opzioni spuntabili
            for (ProductOptionGroup gruppo : gruppi) {
                // Messaggio di debug per la console
                System.out.println("DEBUG: Sto cercando opzioni per il Gruppo: " + gruppo.getName() + " (ID: " + gruppo.getKey() + ")");
                
                List<ProductOption> opzioni = optionDAO.getProductOptionsByProductOptionGroup(gruppo);
                gruppo.setOptions(opzioni); 
            }
            
            // Mandiamo tutto al file HTML
            request.setAttribute("prodotto", prodotto);
            request.setAttribute("gruppi", gruppi);

            TemplateResult templateEngine = new TemplateResult(getServletContext());
            templateEngine.activate("dettaglio.ftl.html", request, response);

        } catch (Exception ex) {
            ex.printStackTrace(); // Stampa l'errore completo nel log
            handleError(ex, request, response);
        }
    }
}