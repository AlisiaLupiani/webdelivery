package WebMarket.Controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Questo dice a Tomcat: "Se l'utente scrive solo l'indirizzo del sito, questa è la mia Servlet"
@WebServlet(name = "RootServlet", urlPatterns = {""})
public class RootServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Appena arriva, lo rimbalziamo verso la pagina di login
        response.sendRedirect("login");
    }
}