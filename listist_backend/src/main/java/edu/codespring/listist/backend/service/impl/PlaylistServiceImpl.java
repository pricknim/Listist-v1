package edu.codespring.listist.backend.service.impl;

import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.repository.DAOFactory;
import edu.codespring.listist.backend.repository.PlaylistDAO;
import edu.codespring.listist.backend.repository.RepositoryException;
import edu.codespring.listist.backend.service.PlaylistService;
import edu.codespring.listist.backend.service.ServiceException;
import edu.codespring.listist.backend.util.PasswordEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    private final PlaylistDAO dao;

    public PlaylistServiceImpl() {
        dao = DAOFactory.getInstance().getPlaylistDAO();
    }

    @Override
    public void create(Playlist playlist) {
        try {
            if (dao.getByName(playlist.getUserId(), playlist.getName()) != null) {
                LOG.info("Failed to create playlist, playlist with the name " + playlist.getName() + " ; by this user already exists.");
                return;
            }

            dao.create(playlist);

            LOG.info("Successfully created playlist with the name " + playlist.getName() + '.');
        } catch (RepositoryException e) {
            LOG.error("Failed to create Playlist! " + e.getMessage());
            throw new ServiceException("Failed to create Playlist! " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Playlist playlist) {
        try {
            Playlist p = dao.getById(playlist.getId());
            if (p == null) {
                LOG.info("Failed to update playlist with id " + playlist.getId() + "; non-existing playlist.");
                return;
            }

            dao.update(playlist);

            LOG.info("Successfully updated playlist with id " + playlist.getId());
        } catch (RepositoryException e) {
            LOG.error("Failed to update playlist with id " + playlist.getId() + "; " + e.getMessage());
            throw new ServiceException("Failed to update playlist with id " + playlist.getId() + "; " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            dao.delete(id);
        } catch (RepositoryException e) {
            LOG.error("Failed to delete playlist by id " + id + "; " + e.getMessage());
            throw new ServiceException("Failed to delete playlist by id " + id + "; " + e.getMessage(), e);
        }
    }

    @Override
    public void addSong(Playlist p, Song s) {
        try {
            dao.addSong(p,s);
        } catch (RepositoryException e) {
            LOG.error("Failed to add song with id " + s.getId() + " to playlist with id " + p.getId() + " ; " + e.getMessage());
            throw new ServiceException("Failed to add song with id " + s.getId() + " to playlist with id " + p.getId() + " ; " + e.getMessage());
        }
    }

    @Override
    public void removeSong(Playlist p, Song s) {
        try {
            dao.removeSong(p,s);
        } catch (RepositoryException e) {
            LOG.error("Failed to remove song with id " + s.getId() + " from playlist with id " + p.getId() + " ; " + e.getMessage());
            throw new ServiceException("Failed to remove song with id " + s.getId() + " from playlist with id " + p.getId() + " ; " + e.getMessage());
        }
    }

    @Override
    public Playlist getByName(Long userId, String name) {
        try {
            Playlist p = dao.getByName(userId, name);

            LOG.info("Successfully queried playlist with the name " + name + " by user with id " + userId + '.');

            return p;
        } catch (RepositoryException e) {
            LOG.error("Failed to query playlist with the name " + name + " by user with id " + userId + '.' + e.getMessage());
            throw new ServiceException("Failed to query playlist with the name " + name + " by user with id " + userId + '.' + e.getMessage(), e);
        }
    }

    @Override
    public List<Playlist> getByName(String name) {
        try {
            List<Playlist> playlists = dao.getByName(name);

            LOG.info("Successfully queried playlists with the name " + name + '.');

            return playlists;
        } catch (RepositoryException e) {
            LOG.error("Failed to query playlist with the name " + name + '.' + e.getMessage());
            throw new ServiceException("Failed to query playlist with the name " + name + '.' + e.getMessage(), e);
        }
    }

    @Override
    public List<Playlist> getByUser(Long userId) {
        try {
            List<Playlist> playlists = dao.getByUser(userId);

            LOG.info("Successfully queried playlists by the user with id " + userId + '.');

            return playlists;
        } catch (RepositoryException e) {
            LOG.error("Failed to query playlists by the user with id " + userId + '.' + e.getMessage());
            throw new ServiceException("Failed to query playlists by the user with id " + userId + '.' + e.getMessage(), e);
        }
    }

    @Override
    public List<Playlist> getAll() {
        try {
            List<Playlist> playlists = dao.getAll();

            LOG.info("Successfully fetched all playlists.");

            return playlists;
        } catch (RepositoryException e) {
            LOG.error("Failed to fetch all playlists." + e.getMessage());
            throw new ServiceException("Failed to fetch all playlists." + e.getMessage(), e);
        }
    }

    @Override
    public List<Song> getNotInPlaylist(Playlist p) {
        try {
            List<Song> songs = dao.getNotInPlaylist(p);

            LOG.info("Successfully fetched all playlists.");

            return songs;
        } catch (RepositoryException e) {
            LOG.error("Failed to fetch songs not in playlists." + e.getMessage());
            throw new ServiceException("Failed to fetch songs not in playlists." + e.getMessage(), e);
        }
    }
}
