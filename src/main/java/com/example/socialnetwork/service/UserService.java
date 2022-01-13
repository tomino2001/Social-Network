package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.repository.Repository;

public class UserService {
    private final Repository<Long, User> repo;

    public UserService(Repository<Long, User> repo) {
        this.repo = repo;
    }

    public void addUser(User user) {
        repo.save(user);
    }

    public void removeUser(Long id) {
        repo.delete(id);
    }

    public void updateUser(User user) {
        repo.update(user);
    }

    public User findOne(Long id) {
        return repo.findOne(id);
    }

    /**
     * finds an user by name
     *
     * @param firstName
     * @param lastName
     * @return null if the user is not found, otherwise return the user
     */
    public User findByName(String firstName, String lastName) {
        Iterable<User> utilizatori = repo.findAll();
        User user = null;
        for (User u : utilizatori) {
            if (u.getFirstName().equals(firstName) && u.getLastName().equals(lastName)) {
                user = u;
            }
        }
        return user;
    }

    public Iterable<User> getAll() {
        return repo.findAll();
    }
}