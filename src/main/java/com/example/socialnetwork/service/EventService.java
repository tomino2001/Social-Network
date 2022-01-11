package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Event;
import com.example.socialnetwork.repository.Repository;

public class EventService {
    private final Repository<Long, Event> repo;

    public EventService(Repository<Long, Event> repo) {
        this.repo = repo;
    }

    public Event saveNotification(Event event) {
        return repo.save(event);
    }

    public void updateNotification(Event event) {
        this.repo.update(event);
    }

    public void removeNotifcation(Event event){this.repo.remove(event);}

    public Event find_one(Long id) {
        return repo.findOne(id);
    }

    public Iterable<Event> getAll(){
        return repo.findAll();
    }
}

