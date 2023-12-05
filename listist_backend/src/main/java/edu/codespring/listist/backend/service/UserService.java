package edu.codespring.listist.backend.service;

import edu.codespring.listist.backend.model.User;

import java.util.List;

public interface UserService {
    void register(User user);
    boolean login(User user);

    void delete(Long id);
    User getById(Long id);
    User getByEmail(String email);
    User getByUsername(String username);
    List<User> getAll();
    void update(User user);
}
