package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    private Utilizator from;
    private List<Utilizator> to;
    private String message;
    private LocalDateTime date;
    private boolean replied;

    public Message(Utilizator from, List<Utilizator> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        replied = false;
        this.date = date;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
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