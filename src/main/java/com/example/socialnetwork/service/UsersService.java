package com.example.socialnetwork.service;

import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.domain.User;

public class UsersService {
    private final Repository<Long, User> repo;

    public UsersService(Repository<Long, User> repo) {
        this.repo = repo;
    }

    public void addUtilizator(User user) {
        repo.save(user);
    }

    public void removeUtilizator(Long id) {
        repo.delete(id);
    }

    public void updateUtilizator(User user) {
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