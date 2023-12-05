package edu.codespring.listist.web;

import com.github.jknack.handlebars.Template;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/usermain")
public class UserMainServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MainServlet.class);

    @Override
    public void destroy(){
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("GET Request arrived to user servlet.");

        Map<String, Object> model = new ConcurrentHashMap<>();
        model.put("message", "smt");

        Template template = HandlebarsTemplateFactory.getTemplate("userMain");
        template.apply(model, resp.getWriter());
    }
}
