package com.example.socialnetwork.ui;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.MesajeService;
import com.example.socialnetwork.service.PrietenieService;
import com.example.socialnetwork.service.UtilizatorService;
import com.example.socialnetwork.service.UtilizatoriPrieteniiService;

import java.time.LocalDateTime;
import java.util.*;

public class ConsolaUtilizator extends Console {
    private final Utilizator utilizatorLogat;
    private final Scanner scanner = new Scanner(System.in);

    public ConsolaUtilizator(Utilizator utilizatorLogat,
                             UtilizatorService utilizatorService, PrietenieService prietenieService,
                             UtilizatoriPrieteniiService utilizatoriPrieteniiService, MesajeService mesajeService) {
        super(utilizatorService, prietenieService, utilizatoriPrieteniiService, mesajeService);
        this.utilizatorLogat = utilizatorLogat;
    }

    private void run_trimite_mesaj() {
        List<Utilizator> utilizatorList = new ArrayList<>();
        System.out.println("Dati numele destinatarilor: ");
        while (true) {
            String first_name = readFirstName();
            String last_name = readLastName();
            Utilizator utilizator = utilizatorService.findByName(first_name, last_name);
            if (utilizator == null) try {
                throw new Exception("Nu exista utilizatorul cu numele si prenumele dat.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            utilizatorList.add(utilizator);

            scanner.nextLine();
            System.out.println("Doriti sa mai adaugati un utilizator la lista de destinatari? (y/n).");
            String optiune = scanner.nextLine();
            if (optiune.equals("n")) break;
        }

        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        Message message = new Message(utilizatorLogat, utilizatorList, mesaj, LocalDateTime.now());
        mesajeService.saveMessage(message);
    }

    public void run_vizualizare_mesaje_primite() {
        System.out.println(mesajeService.find_all_msg_recived_by_user(utilizatorLogat));
    }

    public void run_raspunde_la_mesaj() {
        System.out.println("Dati id-ul mesajului la care doriti sa raspundeti: ");
        Long id = scanner.nextLong();
        Message message = mesajeService.find_one(id);

        scanner.nextLine();
        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        Message message1 = new Message(utilizatorLogat, List.of(message.getFrom()), mesaj, LocalDateTime.now());
        mesajeService.saveMessage(message1);
        mesajeService.updateMessage(message);
    }

    public void run_vezi_cereri_prietenie() {
        System.out.println("Cereri primite de la:");
        prietenieService.listaCereriPrietenieUtilizator(this.utilizatorLogat)
                .forEach(prietenie -> System.out.println(utilizatorService.findOne(prietenie.getId().getLeft())));
        System.out.println();
    }

    public void run_gestioneaza_cerere_prietenie(String status) {
        System.out.println("Introduceti prenumele si numele utilizatorului:");
        String firstName = readFirstName();
        String lastName = readLastName();
        Utilizator utilizatorSursa = utilizatorService.findByName(firstName, lastName);
        if (utilizatorSursa == null) {
            System.out.println("Nu exista utilizatorul");
            return;
        }
        Prietenie prietenie = prietenieService.findOnePrietenie(utilizatorSursa.getId(), utilizatorLogat.getId());
        if (prietenie == null)
            System.out.println("Nu aveti cerere de la utilizatorul introdus");
        else {
            if (Objects.equals(status, "rejected"))
                prietenieService.removePrietenie(utilizatorSursa.getId(), utilizatorLogat.getId());
            else {
                prietenie.setStatus(status);
                prietenie.setDate(LocalDateTime.now());
                prietenieService.updatePrietenie(prietenie);
            }
        }
    }

    public void run_trimite_cerere_prietenie() {
        System.out.println("Cui doriti sa trimiteti cerere?");
        String firstName = readFirstName();
        String lastName = readLastName();
        Utilizator utilizatorDestinatie = utilizatorService.findByName(firstName, lastName);
        if (utilizatorDestinatie == null) {
            System.out.println("Nu exista utilizatorul!");
            return;
        }
        Prietenie prietenie = new Prietenie(utilizatorLogat.getId(), utilizatorDestinatie.getId(),
                LocalDateTime.now());
        prietenie.setStatus("pending");
        if (prietenieService.addPrietenie(prietenie) != null) {
            System.out.println("Prietenia este deja inregistrata");
        }
    }

    private void run_replay_all_mesaj() {
        System.out.println("Dati id-ul mesajului la care doriti sa raspundeti: ");
        Long id = scanner.nextLong();
//        Message message = mesajeService.find_one(id);
        if(mesajeService.find_one(id) == null){
            System.out.println("Nu exista mesajul cu id-ul daat!");
            return;
        }

        scanner.nextLine();
        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        mesajeService.reply_all(utilizatorLogat, id, mesaj);
//        Message message1 = new Message(utilizatorLogat, List.of(message.getFrom()), mesaj, LocalDateTime.now());
//
//
//        mesajeService.saveMessage(message1);
//        mesajeService.updateMessage(message)
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
