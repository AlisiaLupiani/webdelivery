package WebMarket.Controller;


import java.util.List;

import WebMarket.data.dao.ProductDAO;
import WebMarket.data.dao.ProductOptionDAO;
import WebMarket.data.dao.ProductOptionGroupDAO;
import framework.data.DataLayer;
import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.ProductOption;
import model.ProductOptionGroup;

@jakarta.servlet.annotation.WebServlet(name = "DettaglioServlet", urlPatterns = {"/dettaglio"})
public class DettaglioServlet extends WebDeliveryBaseController {

   
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