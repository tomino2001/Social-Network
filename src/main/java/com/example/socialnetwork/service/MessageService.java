package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.repository.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService {
    private final Repository<Long, Message> repo;

    public MessageService(Repository<Long, Message> repo) {
        this.repo = repo;
    }

    public Message saveMessage(Message message) {
        return repo.save(message);
    }

    public void updateMessage(Message message) {
        this.repo.update(message);
    }

    public void removeMessage(Message message){this.repo.remove(message);}

    public Message findOne(Long id) {
        return repo.findOne(id);
    }

    public List<Message> findConversationBetweenUsersChronologically(User user1, User user2) {
        List<Message> messageList = new ArrayList<>();
        this.repo.findAll().forEach(message -> {
            if ((message.getFrom().getId().equals(user1.getId()) && message.getTo().get(0).getId().equals(user2.getId()))
                    || (message.getFrom().getId().equals(user2.getId()) && message.getTo().get(0).getId().equals(user1.getId()))) {
                messageList.add(message);
            }
        });
        messageList.sort((a, b) -> a.getId().compareTo(b.getId()));
        return messageList;
    }

    public List<Message> findAllMessagesReceivedByUser(User userLogat) {
        List<Message> messageList = new ArrayList<>();
        this.repo.findAll().forEach(message -> {
            User user = message.getTo().get(0);
            if (Objects.equals(user.getId(), userLogat.getId()))
                messageList.add(message);
        });

        return messageList;
    }

    public List<Message> findMessagesByIdSentAndDate(Long id_sent, LocalDateTime date) {
        List<Message> messageList = new ArrayList<>();
        for (Message message : repo.findAll()) {
            if (message.getFrom().getId().equals(id_sent) && message.getDate().equals(date)) {
                messageList.add(message);
            }
        }
        return messageList;
    }

    public void replyAll(User userLogat, Long id_msg, String mesaj) {
        Message message = repo.findOne(id_msg);
        List<Message> messageList = findMessagesByIdSentAndDate(message.getFrom().getId(), message.getDate());
        for (Message msg : messageList) {
            List<User> id_to = new ArrayList<>();
            if (userLogat.getId().equals(msg.getTo().get(0).getId())) id_to.add(msg.getFrom());
            else id_to.add(msg.getTo().get(0));
            Message message1 = new Message(userLogat, id_to, mesaj, LocalDateTime.now());
            repo.save(message1);
            repo.update(msg);
        }
    }

    public List<Message> messageListReceivedDuringPeriod(User user, LocalDate st, LocalDate dr){
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(message -> (message.getTo().get(0).equals(user))
                        && message.getDate().toLocalDate().isAfter(st) && message.getDate().toLocalDate().isBefore(dr))
                .collect(Collectors.toList());
    }

    public List<Message> messageListFromUserDuringPeriod(User loggedUser, User user,
                                                         LocalDate st, LocalDate dr){
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(message -> (message.getFrom().equals(user) &&
                        message.getTo().get(0).equals(loggedUser))
                        && message.getDate().toLocalDate().isAfter(st) && message.getDate().toLocalDate().isBefore(dr))
                .collect(Collectors.toList());
    }
}