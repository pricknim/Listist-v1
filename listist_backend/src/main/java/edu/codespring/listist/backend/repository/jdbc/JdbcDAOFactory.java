package edu.codespring.listist.backend.repository.jdbc;

import edu.codespring.listist.backend.repository.DAOFactory;
import edu.codespring.listist.backend.repository.PlaylistDAO;
import edu.codespring.listist.backend.repository.SongDAO;
import edu.codespring.listist.backend.repository.UserDAO;

public class JdbcDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() { return new JdbcUserDAO(); }

    @Override
    public SongDAO getSongDAO() {
        return new JdbcSongDAO();
    }

    @Override
    public PlaylistDAO getPlaylistDAO() { return new JdbcPlaylistDAO(); }
}
