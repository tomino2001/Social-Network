package com.example.socialnetwork.ui;


import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.MessagesService;
import com.example.socialnetwork.service.FriendshipsService;
import com.example.socialnetwork.service.UsersService;
import com.example.socialnetwork.service.GlobalService;

import java.security.KeyException;
import java.time.LocalDateTime;
import java.util.*;

public class Console {
    protected final UsersService usersService;
    protected final FriendshipsService friendshipsService;
    protected final GlobalService globalService;
    protected final MessagesService messagesService;
    private Scanner scanner = new Scanner(System.in);

    public Console(UsersService usersService, FriendshipsService friendshipsService,
                   GlobalService globalService, MessagesService messagesService) {
        this.usersService = usersService;
        this.friendshipsService = friendshipsService;
        this.globalService = globalService;
        this.messagesService = messagesService;
    }


    protected Long readPrietenieID1() {
        System.out.println("Id1: ");
        return scanner.nextLong();
    }

    protected Long readPrietenieID2() {
        System.out.println("Id2: ");
        return scanner.nextLong();
    }

    protected String readFirstName() {
        System.out.println("First name: ");
        return scanner.next();
    }

    protected String readLastName() {
        System.out.println("Last name: ");
        return scanner.next();
    }

    public void run_meniu_CRUD_Utilizator() {
        while (true) {
            System.out.println("1. Add utilizator.");
            System.out.println("2. Delete utilizator.");
            System.out.println("3. Update utilizator.");
            System.out.println("4. Get all utilizatori.");
            System.out.println("5. Find by name.");
            System.out.println("b. Back.");

            System.out.println("Optiune = ");
            String optiune = "";
            optiune = scanner.next();
            if (Objects.equals(optiune, "1")) {
                try {
                    String firstName = readFirstName();
                    String lastName = readLastName();
                    User user = new User(firstName, lastName);
                    this.usersService.addUtilizator(user);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "2")) {
                try {
                    System.out.println("Dati id-ul utilizatoruli de sters: ");
                    Long id = scanner.nextLong();
                    //this.usersService.removeUtilizator(id);
                    globalService.removeUtilizatorAndPrieteniiUtilizator(id);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "3")) {
                try {
                    System.out.println("Id-ul utilizatorului de updatat: ");
                    Long id = scanner.nextLong();
                    String firstName = readFirstName();
                    String lastName = readLastName();
                    User user = new User(firstName, lastName);
                    user.setId(id);
                    this.usersService.updateUtilizator(user);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "4")) {
                try {
                    this.usersService.getAll().forEach(System.out::println);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "5")) {
                String firstName = readFirstName();
                String lastName = readLastName();
                User user = usersService.findByName(firstName, lastName);
                if (user != null)
                    System.out.println(user);
                else
                    System.out.println("Doesn't exist!");
            } else if (Objects.equals(optiune, "b"))
                break;
            else {
                System.out.println("Optiune invalida! Reincercati.");
            }
        }
    }

    public void run_meniu_CRUD_Prietenie() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Add prietenie.");
            System.out.println("2. Delete prietenie.");
            System.out.println("3. Update prietenie.");
            System.out.println("4. Get all prietenie.");
            System.out.println("5. Afisare nr de comunitati.");
            System.out.println("6. Afisare cea mai sociabila comunitate.");
            System.out.println("b. Back.");

            System.out.println("Optiune = ");
            String optiune = "";
            optiune = scanner.next();
            if (Objects.equals(optiune, "1")) {
                try {
                    Long id1 = readPrietenieID1();
                    User user = usersService.findOne(id1);
                    if (user == null) throw new KeyException("Nu exista utilizatorul cu id-ul dat");
                    Long id2 = readPrietenieID2();
                    user = usersService.findOne(id2);
                    if (user == null) throw new KeyException("Nu exista utilizatorul cu id-ul dat");
                    Friendship friendship = new Friendship(id1, id2, LocalDateTime.now());
                    friendship.setStatus("approved");
                    if (this.friendshipsService.addPrietenie(friendship) != null)
                        System.out.println("Prietenia este deja inregistrata");
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "2")) {
                try {
                    System.out.println("Dati id-ul prieteniei de sters: ");
                    long id1 = scanner.nextLong();
                    long id2 = scanner.nextLong();
                    this.friendshipsService.removePrietenie(id1, id2);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "3")) {
                try {
                    Long id1 = readPrietenieID1();
                    Long id2 = readPrietenieID2();
                    Friendship friendship = new Friendship(id1, id2, LocalDateTime.now());
                    friendship.setStatus("approved");
                    this.friendshipsService.updatePrietenie(new Friendship(id1, id2, LocalDateTime.now()));
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "4")) {
                try {
                    this.friendshipsService.getAllApproved().forEach(System.out::println);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "5")) {
                try {

                    System.out.println("Numarul de componete conexe:  " + friendshipsService.numarComponenteConexe());
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "6")) {
                try {
                    System.out.println("Cel mai sociabila comunitate este: ");
                    System.out.println(friendshipsService.celMaiLungDrum());
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "b")) {
                break;
            } else {
                System.out.println("Optiune invalida. Reincercati!");
            }
        }
    }

    public void prieteniiUtilizator() {
        Scanner scanner = new Scanner(System.in);
        String firstName = readFirstName();
        String lastName = readLastName();
        List<Tuple<Long, LocalDateTime>> rezultat = globalService.
                prieteniiUtilizator(firstName, lastName);
        rezultat.forEach(p -> {
            User user = usersService.findOne(p.getLeft());
            System.out.println(user.getLastName() + '|' + user.getFirstName()
                    + '|' + p.getRight().toString());
        });
    }

    public void prieteniiUtilizatorDinLuna() {
        Scanner scanner = new Scanner(System.in);
        String firstName = readFirstName();
        String lastName = readLastName();
        System.out.println("Luna a anului(in engleza): ");
        String monthName = scanner.next();
        List<Tuple<Long, LocalDateTime>> rezultat = globalService.
                prieteniiUtilizatorDinLuna(firstName, lastName, monthName);
        rezultat.forEach(p -> {
            User user = usersService.findOne(p.getLeft());
            System.out.println(user.getLastName() + '|' + user.getFirstName()
                    + '|' + p.getRight().toString());
        });
    }



    public void run_gmail_meniu(){
        System.out.println("Va rugam sa va autentificati.");
        String first_name_log_in = readFirstName();
        String last_name_log_in = readLastName();
        User userLogat = usersService.findByName(first_name_log_in, last_name_log_in);
        if(userLogat == null){
            System.out.println("Nu exista utilizatorul cu numele si prenumele dat.");
            return;
        }
        ConsolaUtilizator consolaUtilizator = new ConsolaUtilizator(userLogat,
                usersService, friendshipsService, globalService, messagesService);
        consolaUtilizator.run_consola_utilizator();
    }

    public void run_show_all_msg_btw_2_users_cronologicaly_ordered(){
        System.out.println("Utilizator1: ");
        String frist_name = readFirstName();
        String last_name = readLastName();
        User user1 = usersService.findByName(frist_name, last_name);

        System.out.println("Utilizator2: ");
        frist_name = readFirstName();
        last_name = readLastName();
        User user2 = usersService.findByName(frist_name, last_name);

        System.out.println(messagesService.find_all_msg_btw_2_users_cronologicaly_ordered(user1, user2));
    }

    public void run_console() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. CRUD Utilizator.");
            System.out.println("2. CRUD Friendship.");
            System.out.println("3. Prietenii utilizator.");
            System.out.println("4. Prietenii utilizator din luna data.");
            System.out.println("5. Log in.");
            System.out.println("6. Toate mesajele intre 2 utilizatori in ordine cronologica.");
            System.out.println("x. Exit.");

            System.out.println("Optiune = ");
            String optiune = "";
            optiune = scanner.next();
            if (Objects.equals(optiune, "1")) {
                run_meniu_CRUD_Utilizator();
            } else if (Objects.equals(optiune, "2")) {
                run_meniu_CRUD_Prietenie();
            } else if (Objects.equals(optiune, "3")) {
                prieteniiUtilizator();
            } else if (Objects.equals(optiune, "4")) {
                prieteniiUtilizatorDinLuna();
            } else if (Objects.equals(optiune, "5")) {
                run_gmail_meniu();
            }else if(Objects.equals(optiune,"6")){
                run_show_all_msg_btw_2_users_cronologicaly_ordered();
            }else if (Objects.equals(optiune, "x")) {
                return;
            } else {
                System.out.println("Optiune invalida. Reincercati!");
            }
        }
    }
}