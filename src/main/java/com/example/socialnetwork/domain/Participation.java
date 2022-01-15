package com.example.socialnetwork.domain;

import java.util.Objects;

public class Participation extends Entity<Tuple<Long, Long>> {
    boolean notifiable;

    public Participation(Long id_user, Long id_event, boolean notifiable) {
        super.setId(new Tuple<>(id_user, id_event));
        this.notifiable = notifiable;
    }

    public boolean isNotifiable() {
        return notifiable;
    }

    @Override
    public String toString() {
        return "Participation{ " + super.getId().toString() +
                " notifications=" + notifiable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participation that = (Participation) o;
        return notifiable == that.notifiable && super.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(notifiable);
    }
}