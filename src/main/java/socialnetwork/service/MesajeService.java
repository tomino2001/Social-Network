package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.repository.Repository;

public class MesajeService {
    private final Repository<Long, Message> repo;

    public MesajeService(Repository<Long, Message> repo) {
        this.repo = repo;
    }

    public Message saveMessage(Message message){
        return repo.save(message);
    }
}