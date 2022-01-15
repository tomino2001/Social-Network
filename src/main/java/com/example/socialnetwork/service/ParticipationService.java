package com.example.socialnetwork.service;


import com.example.socialnetwork.domain.Participation;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.repository.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

public class ParticipationService {
    private final Repository<Tuple<Long, Long>, Participation> repo;

    public ParticipationService(Repository<Tuple<Long, Long>, Participation> repo) {
        this.repo = repo;
    }

    public Participation saveParticipation(Participation participation) {
        return repo.save(participation);
    }

    public void updateParticipation(Participation participation) {
        this.repo.update(participation);
    }

    public void deleteParticipation(Tuple<Long, Long> participationId) {
        this.repo.delete(participationId);
    }

    public Participation findOne(Tuple<Long, Long> id) {
        return repo.findOne(id);
    }

    public List<Participation> getAllUserParticipations(User user, boolean notifiable) {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(participation -> {
                    if (notifiable)
                        return participation.getId().getLeft().equals(user.getId()) && participation.isNotifiable();
                    return participation.getId().getLeft().equals(user.getId());
                })
                .toList();
    }
}