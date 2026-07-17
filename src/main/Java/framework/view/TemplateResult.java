package framework.view;

import freemarker.core.HTMLOutputFormat;
import freemarker.ext.jakarta.servlet.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        cfg = new Configuration(Configuration.VERSION_2_3_34);

        String encoding = "UTF-8";
        if (context.getInitParameter("view.encoding") != null) {
            encoding = context.getInitParameter("view.encoding");
        }
        cfg.setOutputEncoding(encoding);
        cfg.setDefaultEncoding(encoding);

        String templateDirectory = "/WEB-INF/template";
        if (context.getInitParameter("view.template_directory") != null) {
            templateDirectory = context.getInitParameter("view.template_directory");
        }
        cfg.setTemplateLoader(new WebappTemplateLoader(context, templateDirectory));

        if ("true".equals(context.getInitParameter("view.debug"))) {
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        } else {
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        }

        cfg.setOutputFormat(HTMLOutputFormat.INSTANCE);
    }

    public void activate(String templateName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding(cfg.getOutputEncoding());

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
