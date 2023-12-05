package edu.codespring.listist.backend.repository;

import edu.codespring.listist.backend.model.Song;
import edu.codespring.listist.backend.model.User;

import java.util.List;

public interface UserDAO {

    User create(User user);
    void update(User user);
    void delete(Long id);
    User getById(Long id);
    User getByUsername(String username);
    User getByEmail(String email);
    List<User> getAll();
}
