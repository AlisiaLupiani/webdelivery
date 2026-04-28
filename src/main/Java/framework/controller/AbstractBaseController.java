package framework.controller;

import framework.data.DataLayer;
import framework.security.SecurityHelpers;
import framework.utils.ServletHelpers;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 *
 * @author Giuseppe Della Penna
 */
public abstract class AbstractBaseController extends HttpServlet {

    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    //create your own datalayer derived class
    protected abstract DataLayer createDataLayer(DataSource ds) throws ServletException;

    //override to init other information to offer to all the servlets
    protected void initRequest(HttpServletRequest request, DataLayer dl) {
        String completeRequestURL = request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        request.setAttribute("thispageurl", completeRequestURL);
        request.setAttribute("datalayer", dl);
    }

    //override to enforce your policy and/or change the login url
    protected void accessCheckLoginFailed(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        String completeRequestURL = request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        response.sendRedirect("login?referrer=" + URLEncoder.encode(completeRequestURL, "UTF-8"));
    }

    protected void accessCheckRolesFailed(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        handleError("Your roles do not grant access to this resource!", request, response);
    }

    //override to provide your login information in the request
    protected void accessCheckSuccessful(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        HttpSession s = request.getSession(false);
        if (s != null) {
            Map<String, Object> li = new HashMap<>();
            request.setAttribute("logininfo", li);
            li.put("session-start-ts", s.getAttribute("session-start-ts"));
            li.put("username", s.getAttribute("username"));
            li.put("userid", s.getAttribute("userid"));
            li.put("roles", s.getAttribute("roles"));
            li.put("ip", s.getAttribute("ip"));
        }
    }

    ////////////////////////////////////////////////
    private void processBaseRequest(HttpServletRequest request, HttpServletResponse response) {
        //check the session data
        HttpSession s = SecurityHelpers.checkSession(request);
        //creating the datalayer opens the actual (per-request) connection to the shared datasource
        try (DataLayer datalayer = createDataLayer((DataSource) getServletContext().getAttribute("datasource"))) {
            datalayer.init();
            initRequest(request, datalayer);
            //check the access rules for this resource
            if (hasLoggedAccess(request, response)) {
                if (s != null) {
                    if (!checkAccessRoles(request, response)) {
                        accessCheckRolesFailed(request, response);
                        return;
                    }
                } else {
                    accessCheckLoginFailed(request, response);
                    return;
                }
            }
            accessCheckSuccessful(request, response);
            processRequest(request, response);
        } catch (Exception ex) {
            handleError(ex, request, response);
        }
    }

    protected boolean hasLoggedAccess(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        String uri = request.getRequestURI();
        Pattern protect = (Pattern) getServletContext().getAttribute("protect_pattern");
        return protect.matcher(uri).find();
    }

    protected boolean checkAccessRoles(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        HttpSession s = request.getSession(false);
        String uri = request.getRequestURI();
        Map<Pattern, String[]> role_access_patterns = (Map<Pattern, String[]>) getServletContext().getAttribute("role_access_patterns");
        List<String> allowed_roles = role_access_patterns.entrySet().stream()
                .flatMap((entry) -> ((entry.getKey().matcher(uri).find()) ? Arrays.stream(entry.getValue()) : Stream.empty()))
                .distinct().toList();

        return (allowed_roles.isEmpty()
                || (s != null && allowed_roles.stream().filter(((List<String>) s.getAttribute("roles"))::contains).findAny().isPresent()));
    }

    //helper to check if the current user has a particular role, useful to further restrict to a role
    //only particular actions of a controller
    protected boolean checkRole(HttpServletRequest request, String role) {
        HttpSession s = request.getSession(false);
        return (s != null && (((List<String>) s.getAttribute("roles")).contains(role)));
    }

    protected void handleError(String message, HttpServletRequest request, HttpServletResponse response) {
        ServletHelpers.handleError(message, request, response, getServletContext());
    }

    protected void handleError(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        ServletHelpers.handleError(exception, request, response, getServletContext());
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response) {
        ServletHelpers.handleError(request, response, getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processBaseRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processBaseRequest(request, response);
    }

}
