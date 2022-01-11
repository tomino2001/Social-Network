package com.example.socialnetwork.service;


import com.example.socialnetwork.domain.Notification;
import com.example.socialnetwork.repository.Repository;

public class NotificationService {
    private final Repository<Long, Notification> repo;

    public NotificationService(Repository<Long, Notification> repo) {
        this.repo = repo;
    }

    public Notification saveNotification(Notification notification) {
        return repo.save(notification);
    }

    public void updateNotification(Notification notification) {
        this.repo.update(notification);
    }

    public void removeNotifcation(Notification notification){this.repo.remove(notification);}

    public Notification find_one(Long id) {
        return repo.findOne(id);
    }

    public Iterable<Notification> get_all(){return repo.findAll();}
}
