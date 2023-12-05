package edu.codespring.listist.backend.repository.jdbc;

import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.repository.RepositoryException;
import edu.codespring.listist.backend.repository.UserDAO;
import edu.codespring.listist.backend.util.PropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDAO implements UserDAO {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcUserDAO.class);

    private final ConnectionManager connManager;

    public JdbcUserDAO() {
        connManager = ConnectionManager.getInstance();
        boolean createTables = Boolean.parseBoolean(PropertyProvider.getProperty("jdbc_create_tables"));
        if(createTables) {
            LOG.info("Creating table USER");

            Connection c = null;

            try {
                c = connManager.getConnection();
                Statement stm = c.createStatement();
                stm.executeUpdate("CREATE TABLE IF NOT EXISTS USER (" +
                        "id BIGINT(3) PRIMARY KEY AUTO_INCREMENT," +
                        " uuid VARCHAR(127) NOT NULL," +
                        " username VARCHAR(25) NOT NULL," +
                        " password VARCHAR(100) NOT NULL," +
                        " email VARCHAR(25) NOT NULL)" );
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
    public User create(User user) {
        Connection c = null;
        try {
            c = connManager.getConnection();

            PreparedStatement checkUsername = c.prepareStatement("SELECT * FROM user WHERE `username` = ?;");
            PreparedStatement checkEmail = c.prepareStatement("SELECT * FROM user WHERE `email` = ?;");
            checkUsername.setString(1, user.getUsername());
            checkEmail.setString(1, user.getEmail());

            ResultSet rs1 = checkUsername.executeQuery();
            ResultSet rs2 = checkEmail.executeQuery();

            if(rs1.next()) {
                LOG.info("Username " + user.getUsername() + " is already in use.");
                return null;
            }

            if(rs2.next()) {
                LOG.info("Email " + user.getEmail() + " is already in use.");
                return null;
            }

            PreparedStatement stmt = c.prepareStatement("INSERT INTO user (`uuid`, `username`, `password`, `email`) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUuid());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();

            user.setId(rs.getLong(1));
            return user;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to insert User.", e);
            throw new RepositoryException("Failed to insert User.", e);
        } finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public void update(User user) {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("UPDATE user SET `username` = ?, `password` = ?, `email` = ? WHERE `id` = ?;");

            PreparedStatement checkUsername = c.prepareStatement("SELECT * FROM user WHERE `username` = ?;");
            PreparedStatement checkEmail = c.prepareStatement("SELECT * FROM user WHERE `email` = ?;");
            checkUsername.setString(1, user.getUsername());
            checkEmail.setString(1, user.getEmail());

            ResultSet rs1 = checkUsername.executeQuery();
            ResultSet rs2 = checkEmail.executeQuery();

            if(rs1.next()) {
                LOG.info("Username " + user.getUsername() + " is already in use.");
                return;
            }

            if(rs2.next()) {
                LOG.info("Email " + user.getEmail() + " is already in use.");
                return;
            }

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setLong(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to update User.", e);
            throw new RepositoryException("Failed to update User.", e);
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
            PreparedStatement stmt = c.prepareStatement("DELETE FROM user WHERE `id` = ?;");
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete User.", e);
            throw new RepositoryException("Failed to delete User.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

    @Override
    public User getById(Long id) {
        Connection c = null;
        User u = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM user WHERE `id` = ?;");
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                LOG.info("Non-existing user with ID " + id + '.');
                return null;
            }

            u = new User();
            u.setId(rs.getLong("id"));
            u.setUuid(rs.getString("uuid"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setEmail(rs.getString("email"));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query User.", e);
            throw new RepositoryException("Failed to query User.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

        return u;
    }

    @Override
    public User getByUsername(String username) {
        Connection c = null;
        User u = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM user WHERE `username` = ?;");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                LOG.info("Non-existing user " + username + '.');
                return null;
            }

            u = new User();
            u.setUuid(rs.getString("uuid"));
            u.setId(rs.getLong("id"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setEmail(rs.getString("email"));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query User.", e);
            throw new RepositoryException("Failed to query User.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

        return u;
    }

    @Override
    public User getByEmail(String email) {
        Connection c = null;
        User u = null;

        try {
            c = connManager.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM user WHERE `email` = ?;");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                LOG.info("Non-existing user wtih email  " + email + '.');
                return null;
            }

            u = new User();
            u.setUuid(rs.getString("uuid"));
            u.setId(rs.getLong("id"));
            u.setUsername(rs.getString("username"));
            u.setPassword(rs.getString("password"));
            u.setEmail(rs.getString("email"));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query User.", e);
            throw new RepositoryException("Failed to query User.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }

        return u;
    }

    @Override
    public List<User> getAll() {
        Connection c = null;

        try {
            c = connManager.getConnection();

            PreparedStatement stmt = c.prepareStatement("SELECT * FROM user;");
            ResultSet rs = stmt.executeQuery();

            ArrayList<User> users = new ArrayList<>();
            User tmpUser;

            while(rs.next()) {
                tmpUser = new User();
                tmpUser.setUuid(rs.getString("uuid"));
                tmpUser.setId(rs.getLong("id"));
                tmpUser.setUsername(rs.getString("username"));
                tmpUser.setPassword(rs.getString("password"));
                tmpUser.setEmail(rs.getString("email"));

                users.add(tmpUser);
            }

            return users;

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to fetch all Users.", e);
            throw new RepositoryException("Failed to fetch all Users.", e);
        }
        finally {
            if(c != null) {
                connManager.returnConnection(c);
            }
        }
    }

}
