package WebMarket.Controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;


@WebServlet("/TestServlet")

public class TestServlet extends HttpServlet{

@Override
    protected void doGet(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("messaggio_benvenuto", "Evviva! I template FreeMarker funzionano alla perfezione! 🎉");

            framework.view.TemplateResult templateEngine = new framework.view.TemplateResult(getServletContext());

            templateEngine.activate("home.ftl.html", request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().println("Ops, errore nel caricamento del template: " + ex.getMessage());
        }
    }

    @Override
    protected void doPost(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }



    
}
