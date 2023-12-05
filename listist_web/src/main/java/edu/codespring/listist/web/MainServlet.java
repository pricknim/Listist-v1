package edu.codespring.listist.web;

import com.github.jknack.handlebars.Template;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.applet.Main;

import javax.xml.transform.Templates;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/main")
public class MainServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MainServlet.class);

    @Override
    public void destroy(){
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("GET Request arrived to main servlet.");

        Map<String, Object> model = new ConcurrentHashMap<>();
        model.put("message", "smt");

        Template template = HandlebarsTemplateFactory.getTemplate("main");
        template.apply(model, resp.getWriter());
    }
}
