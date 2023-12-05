package edu.codespring.listist.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Template;
import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.service.ServiceFactory;
import edu.codespring.listist.backend.service.SongService;
import edu.codespring.listist.model.ErrorText;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/songs")
public class SongServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SongServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ServiceFactory sf = ServiceFactory.getInstance();
    private final SongService ss = sf.getSongService();

    @Override
    public void destroy(){
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("GET Request arrived to songs servlet");

        Template template = HandlebarsTemplateFactory.getTemplate("songs");

        Map<String, List<Song>> model = new ConcurrentHashMap<>();
        List<Song> songs = ss.getAll();

        if(songs != null) {
            model.put("song", songs);
        }
        template.apply(model, res.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("POST Request arrived to songs servlet.");
        ErrorText error = new ErrorText();

        Song song = objectMapper.readValue(req.getInputStream(), Song.class);
        Song dummy1 = null;
        dummy1 = ss.getByTitleAndArtist(song.getTitle(), song.getArtist());

        if(dummy1 == null) {
            ss.register(song);
            LOG.info("Registering " + song.getTitle() + " by " + song.getArtist() + " was successful.");
        }
        else {
            error.setError("This song has already been registered!");
        }
        objectMapper.writeValue(res.getOutputStream(), error);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("DELETE Request arrived to songs servlet.");

        Song song = objectMapper.readValue(req.getInputStream(), Song.class);
        Song deleteThis = null;
        deleteThis = ss.getByTitleAndArtist(song.getTitle(), song.getArtist());

        if(deleteThis != null) {
            ss.delete(deleteThis.getId());
            LOG.info("Deletion of " + song.getTitle() + " by " + song.getArtist() + " was successful.");
        }
    }
}
