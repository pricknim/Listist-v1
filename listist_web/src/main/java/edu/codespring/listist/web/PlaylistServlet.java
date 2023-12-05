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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/playlist")
public class PlaylistServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(PlaylistServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ServiceFactory sf = ServiceFactory.getInstance();
    private final PlaylistService ps = sf.getPlaylistService();
    private final SongService ss = sf.getSongService();
    private Playlist p;

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("GET Request arrived to playlist servlet.");


        Template template = HandlebarsTemplateFactory.getTemplate("playlist");

        String name = req.getParameter("name");

        Long userID = (Long) req.getSession().getAttribute("id");

        p = ps.getByName(userID, name);
        List<Song> song = p.getSongs();

        Map<String, Object> songs = new ConcurrentHashMap<>();

        songs.put("name", name);

        if(song != null) {
            songs.put("song", song);
        }

        List<Song> unusedSongs = ps.getNotInPlaylist(p);

        if(unusedSongs != null) {
            songs.put("unused", unusedSongs);
        }

        template.apply(songs, res.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("POST Request arrived to playlist servlet.");
        ErrorText error = new ErrorText();


        Song song = objectMapper.readValue(req.getInputStream(), Song.class);
        Song dummy1 = null;
        dummy1 = ss.getByTitleAndArtist(song.getTitle(), song.getArtist());

        if(dummy1 != null) {
            ps.addSong(p, dummy1);
            LOG.info("Adding song " + song.getTitle() + " by " + song.getArtist() + " to playlist " + p.getName() + " was successful!");
        }
        else {
            error.setError("This song doesn't exist in the database!");
        }
        objectMapper.writeValue(res.getOutputStream(), error);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("DELETE Request arrived to playlist servlet.");
        ErrorText error = new ErrorText();
        Song song = objectMapper.readValue(req.getInputStream(), Song.class);
        Song deleteThis = null;
        deleteThis = ss.getByTitleAndArtist(song.getTitle(), song.getArtist());

        if(deleteThis != null) {
            ps.removeSong(p, deleteThis);
            LOG.info("Deletion of " + song.getTitle() + " by " + song.getArtist() + " from playlist " + p.getName() +" was successful.");
        }
        else {
            error.setError("Failed to remove song!");
        }
        objectMapper.writeValue(res.getOutputStream(), error);
    }
}
