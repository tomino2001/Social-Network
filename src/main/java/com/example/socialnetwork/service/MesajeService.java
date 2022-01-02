package com.example.socialnetwork.service;

import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Utilizator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MesajeService {
    private final Repository<Long, Message> repo;

    public MesajeService(Repository<Long, Message> repo) {
        this.repo = repo;
    }

    public Message saveMessage(Message message) {
        return repo.save(message);
    }

    public void updateMessage(Message message) {
        this.repo.update(message);
    }

    public void removeMessage(Message message){this.repo.remove(message);}

    public Message find_one(Long id) {
        return repo.findOne(id);
    }

    public List<Message> find_all_msg_btw_2_users_cronologicaly_ordered(Utilizator utilizator1, Utilizator utilizator2) {
        List<Message> messageList = new ArrayList<>();
        this.repo.findAll().forEach(message -> {
            if ((message.getFrom().getId().equals(utilizator1.getId()) && message.getTo().get(0).getId().equals(utilizator2.getId()))
                    || (message.getFrom().getId().equals(utilizator2.getId()) && message.getTo().get(0).getId().equals(utilizator1.getId()))) {
                messageList.add(message);
            }
        });
        messageList.sort((a, b) -> a.getId().compareTo(b.getId()));
        return messageList;
    }

    public List<Message> find_all_msg_recived_by_user(Utilizator utilizatorLogat) {
        List<Message> messageList = new ArrayList<>();
        this.repo.findAll().forEach(message -> {
            Utilizator utilizator = message.getTo().get(0);
            if (Objects.equals(utilizator.getId(), utilizatorLogat.getId()))
                messageList.add(message);
        });

        return messageList;
    }

    public List<Message> find_by_idSent_and_date(Long id_sent, LocalDateTime data) {
        List<Message> messageList = new ArrayList<>();
        for (Message message : repo.findAll()) {
            if (message.getFrom().getId().equals(id_sent) && message.getDate().equals(data)) {
                messageList.add(message);
            }
        }
        return messageList;
    }

    public void reply_all(Utilizator utilizatorLogat, Long id_msg, String mesaj) {
        Message message = repo.findOne(id_msg);
        List<Message> messageList = find_by_idSent_and_date(message.getFrom().getId(), message.getDate());
        for (Message msg : messageList) {
            List<Utilizator> id_to = new ArrayList<>();
            if (utilizatorLogat.getId().equals(msg.getTo().get(0).getId())) id_to.add(msg.getFrom());
            else id_to.add(msg.getTo().get(0));
            Message message1 = new Message(utilizatorLogat, id_to, mesaj, LocalDateTime.now());
            repo.save(message1);
            repo.update(msg);
        }
    }

    public List<Message> listaMesajePrimiteDinPerioadaX(Utilizator utilizator, LocalDateTime st, LocalDateTime dr){
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(message -> (message.getFrom().equals(utilizator) ||
                        message.getTo().get(0).equals(utilizator))
                        && message.getDate().isAfter(st) && message.getDate().isBefore(dr))
                .collect(Collectors.toList());
    }

    public List<Message> listaMesajePrimiteDeLaUtilizatorXInPerioadaX(Utilizator utilizatorLogat, Utilizator utilizator,
                                                                      LocalDateTime st, LocalDateTime dr){
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(message -> (message.getFrom().equals(utilizatorLogat) ||
                        message.getTo().get(0).equals(utilizator))
                        && message.getDate().isAfter(st) && message.getDate().isBefore(dr))
                .collect(Collectors.toList());
    }
}