package framework.view;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class TemplateResult {
    protected ServletContext context;
    protected Configuration cfg;

    public TemplateResult(ServletContext context) {
        this.context = context;
        init();
    }

    private void init() {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        
        // LA SOLUZIONE: Troviamo noi la cartella per aggirare il problema di Tomcat 10
        try {
            String pathCartella = context.getRealPath("/WEB-INF/template");
            cfg.setDirectoryForTemplateLoading(new File(pathCartella));
        } catch (Exception e) {
            System.err.println("Errore nel trovare la cartella template: " + e.getMessage());
        }
        
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }

    public void activate(String templateName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> datamodel = new HashMap<>();
        Enumeration<String> attrs = request.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String attrName = attrs.nextElement();
            datamodel.put(attrName, request.getAttribute(attrName));
        }

        Template t = cfg.getTemplate(templateName);
        try (Writer out = response.getWriter()) {
            t.process(datamodel, out);
        }
    }
}