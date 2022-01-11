package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Event extends Entity<Long> {
    private String title;
    private String description;
    private final LocalDateTime date;
    private List<Long> users;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getUsers() {
        return users;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public Event(String title, String description, LocalDateTime date, List<Long> users) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.users = users;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date.format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))) +
                ", users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(title, event.title) && Objects.equals(description, event.description) && Objects.equals(date, event.date) && Objects.equals(users, event.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), title, description, date, users);
    }
}
