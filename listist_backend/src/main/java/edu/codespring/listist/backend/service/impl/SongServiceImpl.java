package edu.codespring.listist.backend.service.impl;

import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.repository.DAOFactory;
import edu.codespring.listist.backend.repository.RepositoryException;
import edu.codespring.listist.backend.repository.SongDAO;
import edu.codespring.listist.backend.service.ServiceException;
import edu.codespring.listist.backend.service.SongService;
import edu.codespring.listist.backend.util.PasswordEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SongServiceImpl implements SongService {

    private static final Logger LOG = LoggerFactory.getLogger(SongServiceImpl.class);

    private final SongDAO dao;

    public SongServiceImpl() {
        dao = DAOFactory.getInstance().getSongDAO();
    }
    @Override
    public void register(Song song) {
        try {
            if (dao.getByTitleAndArtist(song.getTitle(), song.getArtist())!= null) {
                LOG.info("Failed to register song, song with title " + song.getTitle() + " by artist " + song.getArtist() + " already exists.");
                return;
            }

            dao.create(song);

            LOG.info("Successfully created song with title " + song.getTitle() + " by artist " + song.getArtist() + '.');
        } catch (RepositoryException e) {
            LOG.error("Failed to register song! " + e.getMessage());
            throw new ServiceException("Failed to register song! " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Song song) {
        try {
            Song s = dao.getById(song.getId());
            if (s == null) {
                LOG.info("Failed to update song with id " + song.getId() + "; non-existing song.");
                return;
            }

            dao.update(song);

            LOG.info("Successfully updated song with id " + song.getId());
        } catch (RepositoryException e) {
            LOG.error("Failed to update song with id " + song.getId() + "; " + e.getMessage());
            throw new ServiceException("Failed to update song with id " + song.getId() + "; " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            dao.delete(id);
        } catch (RepositoryException e) {
            LOG.error("Failed to delete song by id " + id + "; " + e.getMessage());
            throw new ServiceException("Failed to delete song by id " + id + "; " + e.getMessage(), e);
        }
    }

    @Override
    public Song getById(Long id) {
        try {
            Song s = dao.getById(id);

            if (s == null) {
                LOG.info("Non-existing song with id " + id);
            } else {
                LOG.info("Successfully queried song with id " + id);
            }

            return s;
        } catch (RepositoryException e) {
            LOG.error("Failed to query song with id " + id + "; " + e.getMessage());
            throw new ServiceException("Failed to query song with id " + id + "; " + e.getMessage(), e);
        }
    }

    @Override
    public List<Song> getByTitle(String title) {
        try {
            List<Song> songs = dao.getByTitle(title);

            LOG.info("Successfully queried songs with title " + title + '.');

            return songs;
        } catch (RepositoryException e) {
            LOG.error("Failed to query songs with title " + title + '.' + e.getMessage());
            throw new ServiceException("Failed to query songs with title " + title + '.' + e.getMessage(), e);
        }
    }

    @Override
    public Song getByTitleAndArtist(String title, String artist) {
        try {
            Song s = dao.getByTitleAndArtist(title, artist);

            LOG.info("Successfully queried songs with title " + title + " by " + artist + '.');

            return s;
        } catch (RepositoryException e) {
            LOG.error("Failed to query songs with title " + title + " by " + artist + '.' + e.getMessage());
            throw new ServiceException("Failed to query songs with title " + title + " by " + artist + '.' + e.getMessage(), e);
        }
    }

    @Override
    public List<Song> getAll() {
        try {
            List<Song> songs = dao.getAll();

            LOG.info("Successfully queried songs.");

            return songs;
        } catch (RepositoryException e) {
            LOG.error("Failed to query all songs! " + e.getMessage());
            throw new ServiceException("Failed to query all songs! " + e.getMessage(), e);
        }
    }

    @Override
    public List<Song> getAllFromArtist(String artist) {
        try {
            List<Song> songs = dao.getAllFromArtist(artist);

            LOG.info("Successfully queried songs by artist " + artist + '.');

            return songs;
        } catch (RepositoryException e) {
            LOG.error("Failed to query songs by artist " + artist + '.' + e.getMessage());
            throw new ServiceException("Failed to query songs by artist " + artist + '.' + e.getMessage(), e);
        }
    }
}
