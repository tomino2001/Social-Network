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
public class Prietenie extends Entity<Tuple<Long, Long>> {

    private LocalDateTime date;
    private String status;

    public Prietenie(Long id1, Long id2) {
        this.setId(new Tuple<>(id1, id2));
    }

    public Prietenie(Long id1, Long id2, LocalDateTime date) {
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
        Prietenie prietenie = (Prietenie) o;
        return Objects.equals(date, prietenie.date)
                && Objects.equals(getId().getLeft(), prietenie.getId().getLeft())
                && Objects.equals(getId().getRight(), prietenie.getId().getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), date);
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                "id=" + getId() +
                ", date=" + date.format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))) +
                ", status=" + status +
                '}';
    }
}