package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MesajeService {
    private final Repository<Long, Message> repo;

    public MesajeService(Repository<Long, Message> repo) {
        this.repo = repo;
    }

    public Message saveMessage(Message message) {
        return repo.save(message);
    }

    public void updateMessage(Message message){
        this.repo.update(message);
    }

    public Message find_one(Long id){
        return repo.findOne(id);
    }

    public List<Message> find_all_msg_recived_by_user(Utilizator utilizatorLogat){
        List<Message> messageList = new ArrayList<>();
        this.repo.findAll().forEach(message -> {
            Utilizator utilizator = message.getTo().get(0);
            if(Objects.equals(utilizator.getId(), utilizatorLogat.getId()))
                messageList.add(message);
        });

        return messageList;
    }
}