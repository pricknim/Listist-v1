package edu.codespring.listist.backend.repository.jdbc;

import edu.codespring.listist.backend.model.Playlist;
import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.repository.PlaylistDAO;
import edu.codespring.listist.backend.repository.RepositoryException;
import edu.codespring.listist.backend.util.PropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPlaylistDAO implements PlaylistDAO {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcPlaylistDAO.class);

    private final ConnectionManager connManager;

    public JdbcPlaylistDAO() {
        connManager = ConnectionManager.getInstance();
        boolean createTables = Boolean.parseBoolean(PropertyProvider.getProperty("jdbc_create_tables"));
        if(createTables) {
            LOG.info("Creating table Playlist");

            Connection c = null;

            try {
                c = connManager.getConnection();
                Statement stm = c.createStatement();
                stm.executeUpdate("CREATE TABLE IF NOT EXISTS Playlist (" +
                        " uuid VARCHAR(127) NOT NULL," +
                        " id BIGINT(8) PRIMARY KEY AUTO_INCREMENT," +
                        " user_id BIGINT(3) NOT NULL," +
                        " name VARCHAR(45) NOT NULL," +
                        " INDEX fk_Playlist_user1_idx (`user_id` ASC) VISIBLE," +
                        " CONSTRAINT `fk_Playlist_user1`" +
                        " FOREIGN KEY (`user_id`)" +
                        " REFERENCES user(`id`)" +
                        " ON DELETE NO ACTION" +
                        " ON UPDATE NO ACTION);");
                LOG.info("Table created successfully.");

                LOG.info("Creating table Playlist_has_Song.");
                Statement stmt = c.createStatement();
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Playlist_has_Song (" +
                        " Playlist_id BIGINT(8) NOT NULL," +
                        " Song_id BIGINT(8) NOT NULL," +
                        " PRIMARY KEY (`Playlist_id`, `Song_id`)," +
                        " INDEX `fk_Playlist_has_Song_Song1_idx` (`Song_id` ASC) VISIBLE," +
                        " INDEX `fk_Playlist_has_Song_Playlist1_idx` (`Playlist_id` ASC) VISIBLE," +
                        " CONSTRAINT `fk_Playlist_has_Song_Playlist1`" +
                        " FOREIGN KEY (`Playlist_id`)" +
                        " REFERENCES Playlist (`id`)" +
                        " ON DELETE NO ACTION" +
                        " ON UPDATE NO ACTION," +
                        " CONSTRAINT `fk_Playlist_has_Song_Song1`" +
                        " FOREIGN KEY (`Song_id`)" +
                        " REFERENCES Song (`id`)" +
                        " ON DELETE NO ACTION" +
                        " ON UPDATE NO ACTION);");
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
    public Playlist create(Playlist playlist) {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement checkStmt = c.prepareStatement("SELECT * FROM Playlist WHERE `name` = ? AND `user_id` = ?;");
            checkStmt.setString(1, playlist.getName());
            checkStmt.setLong(2, playlist.getUserId());

            ResultSet rs1 = checkStmt.executeQuery();
            if(rs1.next()){
                LOG.info("Playlist name is already in use.");
                return null;
            }

            PreparedStatement stmt = c.prepareStatement("INSERT INTO Playlist (`uuid`, `name`, `user_id`) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, playlist.getUuid());
            stmt.setString(2, playlist.getName());
            stmt.setLong(3, playlist.getUserId());
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();

            playlist.setId(rs.getLong(1));

            PreparedStatement songStmt = c.prepareStatement("INSERT INTO Playlist_has_Song (`Playlist_id`, `Song_id`) VALUES (?, ?);");
            songStmt.setLong(1, playlist.getId());
            for(Song s : playlist.getSongs()) {
                songStmt.setLong(2, s.getId());
                songStmt.execute();
            }

            return playlist;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to register Playlist.", e);
            throw new RepositoryException("Failed to register Playlist.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

    }

    @Override
    public void update(Playlist playlist) {
        Connection c = null;
        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("UPDATE Playlist SET `name` = ?, `user_id` = ? WHERE 'id' = ?;");
            stmt.setString(1, playlist.getName());
            stmt.setLong(2, playlist.getUserId());
            stmt.setLong(3, playlist.getId());
            stmt.executeUpdate();

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to update Playlist.", e);
            throw new RepositoryException("Failed to update Playlist.", e);
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
            PreparedStatement stmt2 = c.prepareStatement("DELETE FROM Playlist_has_Song WHERE `playlist_id` = ?;");
            stmt2.setLong(1, id);
            stmt2.execute();

            PreparedStatement stmt = c.prepareStatement("DELETE FROM Playlist WHERE `id` = ?;");
            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete Playlist.", e);
            throw new RepositoryException("Failed to delete Playlist.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void addSong(Playlist p, Song s) {
        Connection c = null;
        try {
            c = connManager.getConnection();
            PreparedStatement checkStmt = c.prepareStatement("SELECT * FROM Playlist_has_Song WHERE `Playlist_id` = ? AND `Song_id` = ?;");
            checkStmt.setLong(1, p.getId());
            checkStmt.setLong(2, s.getId());

            ResultSet rs1 = checkStmt.executeQuery();

            if(rs1.next()){
                LOG.info("Song with id " + s.getId() + " is already on the playlist with id " + p.getId() + '.');
                return;
            }

            PreparedStatement stmt = c.prepareStatement("INSERT INTO Playlist_has_Song (`Playlist_id`, `Song_id`) VALUES (?, ?);");
            stmt.setLong(1, p.getId());
            stmt.setLong(2, s.getId());
            stmt.execute();

            p.addSong(s);

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to add song with id " + s.getId() + " from playlist with id " + p.getId() + '.', e);
            throw new RepositoryException("Failed to add song with id " + s.getId() + " from playlist with id " + p.getId() + '.', e);
        }
        finally {
            connManager.returnConnection(c);
        }
    }

    @Override
    public void removeSong(Playlist p, Song s) {
        Connection c = null;
        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("DELETE FROM Playlist_has_Song WHERE `Playlist_id` = ? AND `Song_id` = ?;");
            stmt.setLong(1, p.getId());
            stmt.setLong(2, s.getId());
            stmt.executeUpdate();

            p.removeSong(s);

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete song with id " + s.getId() + " from playlist with id " + p.getId() + '.', e);
            throw new RepositoryException("Failed to delete song with id " + s.getId() + " from playlist with id " + p.getId() + '.', e);
        }
        finally {
            connManager.returnConnection(c);
        }
    }


    @Override
    public Playlist getById(Long id) {
        Connection c = null;
        Playlist p;
        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Playlist WHERE `id` = ?;");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()){
                LOG.info("Non-existing Playlist with the id " + id + '.');
                return null;
            }

            p = new Playlist();
            p.setUuid(rs.getString("uuid"));
            p.setId(rs.getLong("id"));
            p.setName(rs.getString("name"));
            p.setUserId(rs.getLong("USER_id"));

            PreparedStatement songs = c.prepareStatement("SELECT * FROM Playlist_has_Song JOIN Song ON `Song`.`id` = `Playlist_has_Song`.`Song_id` WHERE `Playlist_id` = ?;");
            songs.setLong(1, p.getId());
            ResultSet songsResult = songs.executeQuery();

            Song s;
            while(songsResult.next()){
                s = new Song();

                s.setUuid(songsResult.getString("uuid"));
                s.setId(songsResult.getLong("Song_id"));
                s.setArtist(songsResult.getString("artist"));
                s.setTitle(songsResult.getString("title"));

                p.addSong(s);
            }

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query Playlist.", e);
            throw new RepositoryException("Failed to query Playlist.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

        return p;
    }

    @Override
    public Playlist getByName(Long userId, String name) {
        Connection c = null;
        Playlist p;
        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Playlist WHERE `user_id` = ? AND `name` = ?;");
            stmt.setLong(1, userId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()){
                LOG.info("Non-existing Playlist with the name " + name + " ; by user " + userId + '.');
                return null;
            }

            p = new Playlist();
            p.setUuid(rs.getString("uuid"));
            p.setId(rs.getLong("id"));
            p.setName(rs.getString("name"));
            p.setUserId(rs.getLong("USER_id"));

            PreparedStatement songs = c.prepareStatement("SELECT * FROM Playlist_has_Song JOIN Song ON `Song`.`id` = `Playlist_has_Song`.`Song_id` WHERE `Playlist_id` = ?;");
            songs.setLong(1, p.getId());
            ResultSet songsResult = songs.executeQuery();

            Song s;
            while(songsResult.next()){
                s = new Song();

                s.setUuid(songsResult.getString("uuid"));
                s.setId(songsResult.getLong("Song_id"));
                s.setArtist(songsResult.getString("artist"));
                s.setTitle(songsResult.getString("title"));
                p.addSong(s);
            }

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query Playlist.", e);
            throw new RepositoryException("Failed to query Playlist.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

        return p;
    }

    @Override
    public List<Playlist> getByName(String name) {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Playlist WHERE `name` = ?;");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()){
                LOG.info("Non-existing Playlist with the name " + name + '.');
                return null;
            }

            ArrayList<Playlist> playlists = new ArrayList<>();
            Playlist p;
            while(rs.next()) {
                p = new Playlist();

                p.setUuid(rs.getString("uuid"));
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setUserId(rs.getLong("USER_id"));

                PreparedStatement songs = c.prepareStatement("SELECT * FROM Playlist_has_Song JOIN Song ON `Song`.`id` = `Playlist_has_Song`.`Song_id` WHERE `Playlist_id` = ?;");
                songs.setLong(1, p.getId());
                ResultSet songsResult = songs.executeQuery();

                Song s;
                while(songsResult.next()){
                    s = new Song();
                    s.setUuid(songsResult.getString("uuid"));
                    s.setId(songsResult.getLong("Song_id"));
                    s.setArtist(songsResult.getString("artist"));
                    s.setTitle(songsResult.getString("title"));
                    p.addSong(s);
                }

                playlists.add(p);
            }



            return playlists;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to fetch all Playlists with the name " + name + '.', e);
            throw new RepositoryException("Failed to to fetch all Playlists with the name " + name + '.', e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Playlist> getByUser(Long userId) {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Playlist WHERE `user_id` = ?;");
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

          /*  if(!rs.next()){
                LOG.info("No playlist exists by the user " + userId + '.');
                return null;
            }

           */

            ArrayList<Playlist> playlists = new ArrayList<>();
            Playlist p;

            while(rs.next()) {
                p = new Playlist();

                p.setUuid(rs.getString("uuid"));
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setUserId(rs.getLong("USER_id"));
                System.out.println(p);
                PreparedStatement songs = c.prepareStatement("SELECT * FROM Playlist_has_Song JOIN Song ON `Song`.`id` = `Playlist_has_Song`.`Song_id` WHERE `Playlist_id` = ?;");
                songs.setLong(1, p.getId());
                ResultSet songsResult = songs.executeQuery();

                Song s;
                while(songsResult.next()){
                    s = new Song();

                    s.setUuid(songsResult.getString("uuid"));
                    s.setId(songsResult.getLong("Song_id"));
                    s.setArtist(songsResult.getString("artist"));
                    s.setTitle(songsResult.getString("title"));
                    p.addSong(s);
                }
                System.out.println(p);
                playlists.add(p);
            }

            return playlists;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to fetch all Playlists created by user " + userId + '.', e);
            throw new RepositoryException("Failed to to fetch all Playlists created by user " + userId + '.', e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Playlist> getAll() {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Playlist;");
            ResultSet rs = stmt.executeQuery();

            ArrayList<Playlist> playlists = new ArrayList<>();
            Playlist p;

            while(rs.next()) {
                p = new Playlist();

                p.setUuid(rs.getString("uuid"));
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setUserId(rs.getLong("USER_id"));

                PreparedStatement songs = c.prepareStatement("SELECT * FROM Playlist_has_Song JOIN Song ON `Song`.`id` = `Playlist_has_Song`.`Song_id` WHERE `Playlist_id` = ?;");
                songs.setLong(1, p.getId());
                ResultSet songsResult = songs.executeQuery();

                Song s;
                while(songsResult.next()){
                    s = new Song();

                    s.setUuid(songsResult.getString("uuid"));
                    s.setId(songsResult.getLong("Song_id"));
                    s.setArtist(songsResult.getString("artist"));
                    s.setTitle(songsResult.getString("title"));
                    p.addSong(s);
                }

                playlists.add(p);
            }



            return playlists;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to fetch all Playlists.", e);
            throw new RepositoryException("Failed to to fetch all Playlists.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Song> getNotInPlaylist(Playlist p) {
        Connection c = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Song WHERE `id` NOT IN (SELECT Playlist_has_Song.song_id FROM Playlist_has_Song WHERE `playlist_id` = ?);");
            stmt.setLong(1,p.getId());
            ResultSet rs = stmt.executeQuery();

            ArrayList<Song> songs = new ArrayList<>();
            Song s;

            while(rs.next()) {
                s = new Song();

                s.setUuid(rs.getString("uuid"));
                s.setId(rs.getLong("id"));
                s.setTitle(rs.getString("title"));
                s.setArtist(rs.getString("artist"));

                songs.add(s);
            }

            return songs;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to fetch all songs not included in the playlist.", e);
            throw new RepositoryException("Failed to to fetch all songs not included in the playlist.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }
}
