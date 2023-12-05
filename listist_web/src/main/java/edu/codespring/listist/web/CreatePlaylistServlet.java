package edu.codespring.listist.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Template;
import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.service.PlaylistService;
import edu.codespring.listist.backend.service.ServiceFactory;
import edu.codespring.listist.backend.service.SongService;
import edu.codespring.listist.model.ErrorText;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jdk.nashorn.internal.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/createplaylist")
public class CreatePlaylistServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(CreatePlaylistServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ServiceFactory sf = ServiceFactory.getInstance();
    private final SongService ss = sf.getSongService();
    private final PlaylistService ps = sf.getPlaylistService();

    @Override
    public void destroy(){
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("GET Request arrived to createplaylist servlet");

        Template template = HandlebarsTemplateFactory.getTemplate("create");

        Map<String, List<Song>> model = new ConcurrentHashMap<>();
        List<Song> songs = ss.getAll();

        if(songs != null) {
            model.put("song", songs);
        }
        template.apply(model, res.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("POST Request arrived to createplaylist servlet");
        ServiceFactory sf = ServiceFactory.getInstance();
        PlaylistService ps = sf.getPlaylistService();
        SongService ss = sf.getSongService();
        ErrorText error = new ErrorText();
        ObjectMapper objectMapper = new ObjectMapper();
        Playlist p = objectMapper.readValue(req.getInputStream(), Playlist.class);
        p.setUserId((Long)req.getSession().getAttribute("id"));
        Playlist dummy = null;
        dummy = ps.getByName(p.getUserId(), p.getName());
        if(dummy == null) {
            List<Song> songs = p.getSongs();

            for (int i = 0; i < songs.size(); i++) {
                Song s = songs.get(i);
                s = ss.getByTitleAndArtist(s.getTitle(), s.getArtist());
                songs.get(i).setId(s.getId());
            }

            ps.create(p);
        }
        else {
            error.setError("You already have a playlist with this name!");
        }

        objectMapper.writeValue(res.getOutputStream(), error);

    }
}
