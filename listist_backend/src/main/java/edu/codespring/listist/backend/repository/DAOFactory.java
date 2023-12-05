package edu.codespring.listist.backend.repository;

import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.repository.jdbc.JdbcDAOFactory;

public abstract class DAOFactory {

    public abstract UserDAO getUserDAO();
    public abstract SongDAO getSongDAO();
    public abstract PlaylistDAO getPlaylistDAO();

    public static DAOFactory getInstance() { return new JdbcDAOFactory(); }
}
