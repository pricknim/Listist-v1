package edu.codespring.listist.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.service.PlaylistService;
import edu.codespring.listist.backend.service.ServiceFactory;
import edu.codespring.listist.backend.service.UserService;
import edu.codespring.listist.backend.service.impl.PlaylistServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/myplaylists")
public class MyPlaylistsServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MyPlaylistsServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ServiceFactory sf = ServiceFactory.getInstance();
    private final UserService us = sf.getUserService();
    private final PlaylistService ps = sf.getPlaylistService();

    @Override
    public void destroy(){
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("GET Request arrived to myplaylsits servlet.");
        HttpSession session = req.getSession();

        Map<String, List<Playlist>> model = new ConcurrentHashMap<>();

        Template template = HandlebarsTemplateFactory.getTemplate("myplaylists");

        //model.put("message", "smt");

         Long userID = (Long) session.getAttribute("id");

        if(userID != null) {
            User user = us.getById(userID);

            List<Playlist> playlists = ps.getByUser(user.getId());
            System.out.println(playlists);
            if(playlists != null) {
                model.put("playlist", playlists);
            }
        }

        template.apply(model, res.getWriter());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("DELETE Request arrived to myplaylists servlet.");

        BufferedReader reader = req.getReader();
        String name = reader.readLine();
        Playlist deleteThis = null;
        deleteThis = ps.getByName((Long)req.getSession().getAttribute("id"), name);
        System.out.println(deleteThis);
        if(deleteThis != null) {
            ps.delete(deleteThis.getId());
            LOG.info("Deletion of playlist " + name + " was successful.");
        }
    }

}
