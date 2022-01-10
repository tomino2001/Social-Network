package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;

    public Message(User from, List<User> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Mesaj{" +
                "id: " + super.getId() +
                ", from: " + from.getFirstName() + " " + from.getLastName() +
                ", to: " + to +                 //.get(0).getFirstName() + " " + to.get(0).getLastName() +
                ", message: " + message +
                ", date: " + date.format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))) +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(message, message1.message) && Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, message, date);
    }
}