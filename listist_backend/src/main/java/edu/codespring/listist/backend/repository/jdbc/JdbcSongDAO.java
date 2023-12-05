package edu.codespring.listist.backend.repository.jdbc;

import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.repository.RepositoryException;
import edu.codespring.listist.backend.repository.SongDAO;
import edu.codespring.listist.backend.util.PropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcSongDAO implements SongDAO {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcSongDAO.class);

    private final ConnectionManager connManager;

    public JdbcSongDAO() {
        connManager  = ConnectionManager.getInstance();
        boolean createTables = Boolean.parseBoolean(PropertyProvider.getProperty("jdbc_create_tables"));
        if(createTables) {
            LOG.info("Creating table Song");

            Connection c = null;

            try {
                c = connManager.getConnection();
                Statement stm = c.createStatement();
                stm.executeUpdate("CREATE TABLE IF NOT EXISTS Song(" +
                        " uuid VARCHAR(127) NOT NULL," +
                        " id BIGINT(8) PRIMARY KEY AUTO_INCREMENT," +
                        " title VARCHAR(45) NOT NULL," +
                        " artist VARCHAR(45) NOT NULL)");
                LOG.info("Table created successfully.");

            } catch (SQLException e) {
                LOG.error("Failed to create table.", e);
                throw new RepositoryException("Failed to create table.", e);
            } finally {
                if(c != null) {
                    connManager.returnConnection(c);
                }
            }
        }

    }

    @Override
    public Song create(Song song) {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement checkStmt = c.prepareStatement("SELECT * FROM Song WHERE `artist` = ? AND `title` = ?;");
            checkStmt.setString(1, song.getArtist());
            checkStmt.setString(2, song.getTitle());
            ResultSet rs1 = checkStmt.executeQuery();
            if(rs1.next()) {
                LOG.info("Song is already registered.");
                return null;
            }

            PreparedStatement stmt = c.prepareStatement("INSERT INTO Song (`uuid`, `title`, `artist`) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, song.getUuid());
            stmt.setString(2, song.getTitle());
            stmt.setString(3, song.getArtist());
            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            song.setId(rs.getLong(1));
            return song;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to register Song.", e);
            throw new RepositoryException("Failed to register Song.", e);
        }
        finally {
            if(c != null) {
              connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void update(Song song) {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement checkStmt = c.prepareStatement("SELECT * FROM Song WHERE `artist` = ? AND `title` = ?;");
            checkStmt.setString(1, song.getArtist());
            checkStmt.setString(2, song.getTitle());
            ResultSet rs1 = checkStmt.executeQuery();
            if(rs1.next()) {
                LOG.info("Song is already registered.");
                return;
            }

            PreparedStatement stmt = c.prepareStatement("UPDATE Song SET `title` = ?, `artist` = ? WHERE `id` = ?;");
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setLong(3, song.getId());
            stmt.executeUpdate();

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to update Song.", e);
            throw new RepositoryException("Failed to update Song.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void delete(Long id) {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement delete = c.prepareStatement("DELETE FROM Playlist_has_Song WHERE `song_id` = ?;");
            delete.setLong(1, id);
            delete.executeUpdate();

            PreparedStatement stmt = c.prepareStatement("DELETE FROM Song WHERE `id` = ?;");
            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete Song.", e);
            throw new RepositoryException("Failed to delete Song.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public Song getById(Long id) {
        Connection c = null;
        Song s = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Song WHERE `id` = ?;");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                LOG.info("Non-existing Song with ID " + id + '.');
                return null;
            }

            s = new Song();
            s.setUuid(rs.getString("uuid"));
            s.setId(rs.getLong("id"));
            s.setTitle(rs.getString("title"));
            s.setArtist(rs.getString("artist"));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query Song.", e);
            throw new RepositoryException("Failed to query Song.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

        return s;
    }

    @Override
    public Song getByTitleAndArtist(String title, String artist) {
        Connection c = null;
        Song s = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Song WHERE `title` = ? AND `artist` = ?;");
            stmt.setString(1, title);
            stmt.setString(2, artist);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                LOG.info("Non-existing Song with title " + title + " by artist " + artist + '.');
                return null;
            }

            s = new Song();
            s.setUuid(rs.getString("uuid"));
            s.setId(rs.getLong("id"));
            s.setTitle(rs.getString("title"));
            s.setArtist(rs.getString("artist"));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query Song.", e);
            throw new RepositoryException("Failed to query Song.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

        return s;
    }

    @Override
    public List<Song> getByTitle(String title) {
        Connection c = null;
        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Song WHERE `title` = ?;");
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Song> songs = new ArrayList<>();
            Song tmpSong;

            while(rs.next()){
                tmpSong = new Song();
                tmpSong.setUuid(rs.getString("uuid"));
                tmpSong.setId(rs.getLong("id"));
                tmpSong.setTitle(rs.getString("title"));
                tmpSong.setArtist(rs.getString("artist"));

                songs.add(tmpSong);
            }

            if(songs.size() == 0) {
                LOG.info("Non-existing song with title " + title + '.');
                return null;
            }

            return songs;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query Song.", e);
            throw new RepositoryException("Failed to query Song.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Song> getAll() {
        Connection c = null;
        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Song;");
            ResultSet rs = stmt.executeQuery();

            ArrayList<Song> songs = new ArrayList<>();
            Song tmpSong;

            while(rs.next()){
                tmpSong = new Song();
                tmpSong.setUuid(rs.getString("uuid"));
                tmpSong.setId(rs.getLong("id"));
                tmpSong.setTitle(rs.getString("title"));
                tmpSong.setArtist(rs.getString("artist"));

                songs.add(tmpSong);
            }

            return songs;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to fetch all Songs.", e);
            throw new RepositoryException("Failed to fetch all Songs.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Song> getAllFromArtist(String artist) {
        Connection c = null;
        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Song WHERE `artist` = ?;");
            stmt.setString(1, artist);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Song> songs = new ArrayList<>();
            Song tmpSong;

            while(rs.next()){
                tmpSong = new Song();
                tmpSong.setUuid(rs.getString("uuid"));
                tmpSong.setId(rs.getLong("id"));
                tmpSong.setTitle(rs.getString("title"));
                tmpSong.setArtist(rs.getString("artist"));

                songs.add(tmpSong);
            }

            if(songs.size() == 0) {
                LOG.info("Non-existing song with title " + artist + '.');
                return null;
            }

            return songs;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query Songs.", e);
            throw new RepositoryException("Failed to query Songs.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }
}
