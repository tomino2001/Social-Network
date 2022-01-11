package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Notification extends Entity<Long> {

    private Long receiver;
    private String message;
    private final LocalDateTime date;

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Notification(Long receiver, String message, LocalDateTime date) {
        this.receiver = receiver;
        this.message = message;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "receiver=" + receiver +
                ", message='" + message + '\'' +
                ", date=" + date.format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(receiver, that.receiver) && Objects.equals(message, that.message) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiver, message, date);
    }
}
