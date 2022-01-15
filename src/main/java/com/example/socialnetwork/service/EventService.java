package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Event;
import com.example.socialnetwork.repository.Repository;

public class EventService {
    private final Repository<Long, Event> repo;

    public EventService(Repository<Long, Event> repo) {
        this.repo = repo;
    }

    public Event saveEvent(Event event) {
        return repo.save(event);
    }

    public Event findOne(Long id) {
        return repo.findOne(id);
    }

    public Iterable<Event> getAll() {
        return repo.findAll();
    }
}

