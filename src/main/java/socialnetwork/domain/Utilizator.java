package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    //private final List<Utilizator> friends = new ArrayList<>();

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    //public List<Utilizator> getFriends() {return friends;}

    //public void setFrient(Utilizator utilizator){friends.add(utilizator);}

    @Override
    public String toString() {
      //  List<Long> frindIdss = new ArrayList<>();
      //  friends.forEach(friend -> frindIdss.add(friend.getId()));

        return "Utilizator{" +
                "id: " + super.getId()  +
                " ,firstName: " + firstName  +
                ", lastName: " + lastName  +
        //        ", friends: " + frindIdss +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()); //&&
    //            getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName()); // getFriends());
    }
}