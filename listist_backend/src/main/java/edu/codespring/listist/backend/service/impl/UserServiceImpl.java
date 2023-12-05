package edu.codespring.listist.backend.service.impl;

import edu.codespring.listist.backend.model.User;
import edu.codespring.listist.backend.repository.DAOFactory;
import edu.codespring.listist.backend.repository.RepositoryException;
import edu.codespring.listist.backend.repository.UserDAO;
import edu.codespring.listist.backend.service.ServiceException;
import edu.codespring.listist.backend.service.UserService;
import edu.codespring.listist.backend.util.PasswordEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class UserServiceImpl implements UserService{

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO dao;

    public UserServiceImpl() {
        dao = DAOFactory.getInstance().getUserDAO();
    }

    @Override
    public void register(User user) {
        try {
            if (dao.getByUsername(user.getUsername()) != null) {
                LOG.info("Failed to register user, user by username " + user.getUsername() + " already exists.");
                return;
            }

            user.setPassword(PasswordEncrypter.generateHashedPassword(user.getPassword(), user.getUuid()));

            dao.create(user);

            LOG.info("Successfully created user with username " + user.getUsername());
        } catch (RepositoryException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            LOG.error("Failed to register user! " + e.getMessage());
            throw new ServiceException("Failed to register user! " + e.getMessage(), e);
        }
    }

    @Override
    public boolean login(User user) {
        User u = null;

        try {
            u = dao.getByUsername(user.getUsername());

            if (u == null) {
                LOG.info("Failed to login user " + user.getUsername());
                return false;
            }

            user.setPassword(PasswordEncrypter.generateHashedPassword(user.getPassword(), u.getUuid()));
            if (u.getPassword().equals(user.getPassword())) {
                LOG.info("Successfully logged in user " + user.getUsername());
                return true;
            } else {
                LOG.info("Failed to login user " + user.getUsername());
                return false;
            }
        } catch (RepositoryException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            LOG.error("Failed to login user! " + e.getMessage());
            throw new ServiceException("Failed to login user! " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            dao.delete(id);
        } catch (RepositoryException e) {
            LOG.error("Failed to delete user by id " + id + "; " + e.getMessage());
            throw new ServiceException("Failed to delete user by id " + id + "; " + e.getMessage(), e);
        }
    }

    @Override
    public User getById(Long id) {
        try {
            User u = dao.getById(id);

            if (u == null) {
                LOG.info("Non-existing user with id " + id);
            } else {
                LOG.info("Successfully queried user with id " + id);
            }

            return u;
        } catch (RepositoryException e) {
            LOG.error("Failed to query user with id " + id + "; " + e.getMessage());
            throw new ServiceException("Failed to query user with id " + id + "; " + e.getMessage(), e);
        }
    }

    @Override
    public User getByEmail(String email) {
        try {
            User u = dao.getByEmail(email);

            if(u == null) {
                LOG.info("Non-existing user with email " + email + '.');
            }
            else {
                LOG.info("Successfully queried user with email " + email + '.');
            }

            return u;
        } catch (RepositoryException e) {
            LOG.error("Failed to query user with email " + email + "; " + e.getMessage());
            throw new ServiceException("Failed to query use with email " + email + "; " + e.getMessage(), e);
        }
    }

    @Override
    public User getByUsername(String username) {
        try {
            User u = dao.getByUsername(username);

            if(u == null) {
                LOG.info("Non-existing user with username " + username + '.');
            }
            else {
                LOG.info("Successfully queried user with username " + username + '.');
            }

            return u;
        } catch (RepositoryException e) {
            LOG.error("Failed to query user with username " + username + "; " + e.getMessage());
            throw new ServiceException("Failed to query use with username " + username + "; " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAll() {
        try {
            List<User> users = dao.getAll();

            LOG.info("Successfully queried users.");

            return users;
        } catch (RepositoryException e) {
            LOG.error("Failed to query all users! " + e.getMessage());
            throw new ServiceException("Failed to query all users! " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        try {
            User u = dao.getByUsername(user.getUsername());
            if (u == null) {
                LOG.info("Failed to update user with username " + user.getUsername() + "; non-existing user.");
                return;
            }

            dao.update(user);

            LOG.info("Successfully updated user with username " + user.getUsername());
        } catch (RepositoryException e) {
            LOG.error("Failed to update user with username " + user.getUsername() + "; " + e.getMessage());
            throw new ServiceException("Failed to update user with username " + user.getUsername() + "; " + e.getMessage(), e);
        }
    }
}
