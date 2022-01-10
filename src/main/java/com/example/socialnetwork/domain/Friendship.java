package com.example.socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Creating a friendship
 *
 * !!!!!!!!!!!!!!!!!!!!!!!
 * User must explicitly specify the status
 * !!!!!!!!!!!!!!!!!!!!!!!
 */
public class Friendship extends Entity<Tuple<Long, Long>> {

    private LocalDateTime date;
    private String status;

    public Friendship(Long id1, Long id2) {
        this.setId(new Tuple<>(id1, id2));
    }

    public Friendship(Long id1, Long id2, LocalDateTime date) {
        this.setId(new Tuple<>(id1, id2));
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getStatus() {
        return status;
    }

    public void setDate(LocalDateTime date){
        this.date = date;
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship friendship = (Friendship) o;
        return Objects.equals(date, friendship.date)
                && Objects.equals(getId().getLeft(), friendship.getId().getLeft())
                && Objects.equals(getId().getRight(), friendship.getId().getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), date);
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + getId() +
                ", date=" + date.format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))) +
                ", status=" + status +
                '}';
    }
}