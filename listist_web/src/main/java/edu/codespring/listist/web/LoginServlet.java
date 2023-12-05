package edu.codespring.listist.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Template;
import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.service.ServiceFactory;
import edu.codespring.listist.backend.service.UserService;
import edu.codespring.listist.model.LoginErrors;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MainServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void destroy(){
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("GET Request arrived to login servlet.");

        Map<String, Object> model = new ConcurrentHashMap<>();
        model.put("message", "smt");

        Template template = HandlebarsTemplateFactory.getTemplate("login");
        template.apply(model, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException{
        LOG.info("POST Request arrived to register servlet.");
        ServiceFactory sf = ServiceFactory.getInstance();
        UserService us = sf.getUserService();
        User user = objectMapper.readValue(req.getInputStream(),User.class);

        User dummy1 = us.getByUsername(user.getUsername());

        res.setContentType("application/json");
        LoginErrors le = new LoginErrors();

        if(dummy1 == null) {
            le.setUsernameError("Incorrect username.");
            LOG.info("User " + user.getUsername() + " doesn't exist.");
        } else {
            if (!us.login(user)) {
                le.setPswError("Incorrect password.");
                LOG.info("Incorrect password.");
            } else {
              LOG.info("User " + user.getUsername() + " has successfully logged in.");
              req.getSession().setAttribute("username", user.getUsername());
              req.getSession().setAttribute("id", dummy1.getId());
            }
        }

        objectMapper.writeValue(res.getOutputStream(), le);
    }
}
