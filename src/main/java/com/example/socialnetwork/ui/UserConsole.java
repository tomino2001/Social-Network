package com.example.socialnetwork.ui;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.MessageService;
import com.example.socialnetwork.service.FriendshipService;
import com.example.socialnetwork.service.UserService;
import com.example.socialnetwork.service.GlobalService;

import java.time.LocalDateTime;
import java.util.*;

public class UserConsole extends Console {
    private final User userLogat;
    private final Scanner scanner = new Scanner(System.in);

    public UserConsole(User userLogat,
                       UserService userService, FriendshipService friendshipService,
                       GlobalService globalService, MessageService messageService) {
        super(userService, friendshipService, globalService, messageService);
        this.userLogat = userLogat;
    }

    private void run_trimite_mesaj() {
        List<User> userList = new ArrayList<>();
        System.out.println("Dati numele destinatarilor: ");
        while (true) {
            String first_name = readFirstName();
            String last_name = readLastName();
            User user = userService.findByName(first_name, last_name);
            if (user == null) try {
                throw new Exception("Nu exista utilizatorul cu numele si prenumele dat.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            userList.add(user);

            scanner.nextLine();
            System.out.println("Doriti sa mai adaugati un utilizator la lista de destinatari? (y/n).");
            String optiune = scanner.nextLine();
            if (optiune.equals("n")) break;
        }

        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        Message message = new Message(userLogat, userList, mesaj, LocalDateTime.now());
        messageService.saveMessage(message);
    }

    public void run_vizualizare_mesaje_primite() {
        System.out.println(messageService.findAllMessagesReceivedByUser(userLogat));
    }

    public void run_raspunde_la_mesaj() {
        System.out.println("Dati id-ul mesajului la care doriti sa raspundeti: ");
        Long id = scanner.nextLong();
        Message message = messageService.findOne(id);

        scanner.nextLine();
        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        Message message1 = new Message(userLogat, List.of(message.getFrom()), mesaj, LocalDateTime.now());
        messageService.saveMessage(message1);
        messageService.updateMessage(message);
    }

    public void run_vezi_cereri_prietenie() {
        System.out.println("Cereri primite de la:");
        friendshipService.userFriendRequestsList(this.userLogat)
                .forEach(prietenie -> System.out.println(userService.findOne(prietenie.getId().getLeft())));
        System.out.println();
    }

    public void run_gestioneaza_cerere_prietenie(String status) {
        System.out.println("Introduceti prenumele si numele utilizatorului:");
        String firstName = readFirstName();
        String lastName = readLastName();
        User userSursa = userService.findByName(firstName, lastName);
        if (userSursa == null) {
            System.out.println("Nu exista utilizatorul");
            return;
        }
        Friendship friendship = friendshipService.findOneFriendship(userSursa.getId(), userLogat.getId());
        if (friendship == null)
            System.out.println("Nu aveti cerere de la utilizatorul introdus");
        else {
            if (Objects.equals(status, "rejected"))
                friendshipService.removeFriendship(userSursa.getId(), userLogat.getId());
            else {
                friendship.setStatus(status);
                friendship.setDate(LocalDateTime.now());
                friendshipService.updateFriendship(friendship);
            }
        }
    }

    public void run_trimite_cerere_prietenie() {
        System.out.println("Cui doriti sa trimiteti cerere?");
        String firstName = readFirstName();
        String lastName = readLastName();
        User userDestinatie = userService.findByName(firstName, lastName);
        if (userDestinatie == null) {
            System.out.println("Nu exista utilizatorul!");
            return;
        }
        Friendship friendship = new Friendship(userLogat.getId(), userDestinatie.getId(),
                LocalDateTime.now());
        friendship.setStatus("pending");
        if (friendshipService.addFriendship(friendship) != null) {
            System.out.println("Prietenia este deja inregistrata");
        }
    }

    private void run_replay_all_mesaj() {
        System.out.println("Dati id-ul mesajului la care doriti sa raspundeti: ");
        Long id = scanner.nextLong();
//        Message message = messageService.find_one(id);
        if(messageService.findOne(id) == null){
            System.out.println("Nu exista mesajul cu id-ul daat!");
            return;
        }

        scanner.nextLine();
        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        messageService.replyAll(userLogat, id, mesaj);
//        Message message1 = new Message(utilizatorLogat, List.of(message.getFrom()), mesaj, LocalDateTime.now());
//
//
//        messageService.saveMessage(message1);
//        messageService.updateMessage(message)
    }

    public void run_consola_utilizator() {
        while (true) {
            System.out.println("1. Trimite mesaj.");
            System.out.println("2. Vizualizare mesaje primite.");
            System.out.println("3. Raspunde la un mesaj.");
            System.out.println("4. Vezi cereri de prietenie.");
            System.out.println("5. Accepta cerere de prietenie.");
            System.out.println("6. Respinge cerere de prietenie.");
            System.out.println("7. Trimite cerere de prietenie.");
            System.out.println("8. Replay-all la un mesaj.");
            System.out.println("b. Back.");

            System.out.println("Optiune = ");
            String optiune;
            optiune = scanner.next();
            if (Objects.equals(optiune, "1")) {
                run_trimite_mesaj();
            } else if (Objects.equals(optiune, "2")) {
                run_vizualizare_mesaje_primite();
            } else if (Objects.equals(optiune, "3")) {
                run_raspunde_la_mesaj();
            } else if (Objects.equals(optiune, "4")) {
                run_vezi_cereri_prietenie();
            } else if (Objects.equals(optiune, "5")) {
                run_gestioneaza_cerere_prietenie("approved");
            } else if (Objects.equals(optiune, "6")) {
                run_gestioneaza_cerere_prietenie("rejected");
            } else if (Objects.equals(optiune, "7")) {
                run_trimite_cerere_prietenie();
            } else if (Objects.equals(optiune, "8")) {
                run_replay_all_mesaj();
            } else if (Objects.equals(optiune, "b")) {
                break;
            } else {
                System.out.println("Optiune invalida. Reincercati!");
            }
        }
    }
}
