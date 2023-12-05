package edu.codespring.listist.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Template;
import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.service.ServiceFactory;
import edu.codespring.listist.backend.service.UserService;
import edu.codespring.listist.model.RegisterErrors;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MainServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void destroy(){
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("GET Request arrived to register servlet.");

        Map<String, Object> model = new ConcurrentHashMap<>();
        model.put("message", "smt");

        Template template = HandlebarsTemplateFactory.getTemplate("register");
        template.apply(model, resp.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("POST Request arrived to register servlet.");
        ServiceFactory sf = ServiceFactory.getInstance();
        UserService us = sf.getUserService();

        User newUser = objectMapper.readValue(req.getInputStream(),User.class);

        User dummy1 = null, dummy2 = null;
        dummy1 = us.getByUsername(newUser.getUsername());
        dummy2 = us.getByEmail(newUser.getEmail());
        boolean ok = true;

        res.setContentType("application/json");
        RegisterErrors re = new RegisterErrors();

        if(dummy1 != null) {
            re.setUsernameError("Username is already in use.");
            ok = false;
        }

        if(dummy2 != null) {
            re.setEmailError("Email is already in use.");
            ok = false;
        }
        if(ok) {
            us.register(newUser);
            LOG.info("User " + newUser.getUsername() + " has been successfully registered.");
        }
        objectMapper.writeValue(res.getOutputStream(), re);
    }
}
