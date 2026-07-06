package WebMarket.Controller;


import framework.view.TemplateResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@jakarta.servlet.annotation.WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends WebDeliveryBaseController {


    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        jakarta.servlet.http.HttpSession existingSession = request.getSession(false);
        if (existingSession != null && existingSession.getAttribute("session-start-ts") == null) {
            existingSession.invalidate(); 
        }
        try {
            
            request.setAttribute("messaggio_benvenuto", "Ciao! Il motore Java e i template stanno comunicando alla perfezione! 🚀");
            
            TemplateResult templateEngine = new TemplateResult(getServletContext());
            templateEngine.activate("home.ftl.html", request, response);
            
        } catch (Exception ex) {
            handleError(ex, request, response);
        }
    }
} 