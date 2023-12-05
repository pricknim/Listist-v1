package edu.codespring.listist.backend.repository.jdbc;

import edu.codespring.listist.backend.repository.RepositoryException;
import edu.codespring.listist.backend.util.PropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);

    private static ConnectionManager instance;
    private List<Connection> pool;
    private static final int POOL_SIZE = Integer.parseInt(PropertyProvider.getProperty("jdbc_pool_size"));

    private ConnectionManager() {
        try {
            Class.forName(PropertyProvider.getProperty("jdbc_driver"));

            pool = new LinkedList<>();

            for(int c = 0; c < POOL_SIZE; ++c) {
                pool.add(DriverManager.getConnection(PropertyProvider.getProperty("jdbc_url"), PropertyProvider.getProperty("jdbc_user"), PropertyProvider.getProperty("jdbc_password")));

            }

        } catch (ClassNotFoundException | SQLException e) {
            LOG.error("Failed to initialize ConnectionManager.", e);
            throw new RepositoryException("Failed to initialize ConnectionManager.", e);
        }
    }

    public static synchronized ConnectionManager getInstance() {
        if(Objects.isNull(instance)) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    public synchronized Connection getConnection() throws RepositoryException {
        Connection c = null;

        if (pool.size() > 0) {
            c = pool.get(0);
            pool.remove(0);
        }

        if (c == null) {
            throw new RepositoryException("Empty connection pool.");
        }

        return c;
    }

    public synchronized void returnConnection(Connection c) {
        if (pool.size() < POOL_SIZE) {
            pool.add(c);
        }
    }
}
