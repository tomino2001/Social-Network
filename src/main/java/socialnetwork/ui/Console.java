package socialnetwork.ui;

import jdk.jfr.internal.test.WhiteBox;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.MesajeService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatoriPrieteniiService;
import socialnetwork.service.UtilizatorService;

import javax.sound.midi.Soundbank;
import java.security.KeyException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class Console {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;
    private final UtilizatoriPrieteniiService utilizatoriPrieteniiService;
    private final MesajeService mesajeService;
    private final Scanner scanner = new Scanner(System.in);

    public Console(UtilizatorService utilizatorService, PrietenieService prietenieService,
                   UtilizatoriPrieteniiService utilizatoriPrieteniiService, MesajeService mesajeService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.utilizatoriPrieteniiService = utilizatoriPrieteniiService;
        this.mesajeService = mesajeService;
    }

    private Long readPrietenieID1() {
        System.out.println("Id1: ");
        return scanner.nextLong();
    }

    private Long readPrietenieID2() {
        System.out.println("Id2: ");
        return scanner.nextLong();
    }

    private Long readUserID() {
        System.out.println("ID: ");
        return scanner.nextLong();
    }

    private String readFirstName() {
        System.out.println("Frist name: ");
        return scanner.next();
    }

    private String readLastName() {
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
                    Utilizator utilizator = new Utilizator(firstName, lastName);
                    this.utilizatorService.addUtilizator(utilizator);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "2")) {
                try {
                    System.out.println("Dati id-ul utilizatoruli de sters: ");
                    Long id = scanner.nextLong();
                    //this.utilizatorService.removeUtilizator(id);
                    utilizatoriPrieteniiService.removeUtilizatorAndPrieteniiUtilizator(id);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "3")) {
                try {
                    System.out.println("Id-ul utilizatorului de updatat: ");
                    Long id = scanner.nextLong();
                    String firstName = readFirstName();
                    String lastName = readLastName();
                    Utilizator utilizator = new Utilizator(firstName, lastName);
                    utilizator.setId(id);
                    this.utilizatorService.updateUtilizator(utilizator);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "4")) {
                try {
                    this.utilizatorService.getAll().forEach(System.out::println);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "5")) {
                String firstName = readFirstName();
                String lastName = readLastName();
                Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
                if (utilizator != null)
                    System.out.println(utilizator);
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
                    Utilizator utilizator = utilizatorService.findOne(id1);
                    if (utilizator == null) throw new KeyException("Nu exista utilizatorul cu id-ul dat");
                    Long id2 = readPrietenieID2();
                    utilizator = utilizatorService.findOne(id2);
                    if (utilizator == null) throw new KeyException("Nu exista utilizatorul cu id-ul dat");
                    this.prietenieService.addPrietenie(new Prietenie(id1, id2, LocalDateTime.now()));
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "2")) {
                try {
                    System.out.println("Dati id-ul prieteniei de sters: ");
                    long id1 = scanner.nextLong();
                    long id2 = scanner.nextLong();
                    this.prietenieService.removePrietenie(id1, id2);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "3")) {
                try {
                    Long id1 = readPrietenieID1();
                    Long id2 = readPrietenieID2();
                    this.prietenieService.updatePrietenie(new Prietenie(id1, id2, LocalDateTime.now()));
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "4")) {
                try {
                    this.prietenieService.getAll().forEach(System.out::println);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "5")) {
                try {

                    System.out.println("Numarul de componete conexe:  " + prietenieService.numarComponenteConexe());
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (Objects.equals(optiune, "6")) {
                try {
                    System.out.println("Cel mai sociabila comunitate este: ");
                    System.out.println(prietenieService.celMaiLungDrum());
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
        List<Tuple<Long, LocalDateTime>> rezultat = utilizatoriPrieteniiService.
                prieteniiUtilizator(firstName, lastName);
        rezultat.forEach(p -> {
            Utilizator utilizator = utilizatorService.findOne(p.getLeft());
            System.out.println(utilizator.getLastName() + '|' + utilizator.getFirstName()
                    + '|' + p.getRight().toString());
        });
    }

    public void prieteniiUtilizatorDinLuna() {
        Scanner scanner = new Scanner(System.in);
        String firstName = readFirstName();
        String lastName = readLastName();
        System.out.println("Luna a anului(in engleza): ");
        String monthName = scanner.next();
        List<Tuple<Long, LocalDateTime>> rezultat = utilizatoriPrieteniiService.
                prieteniiUtilizatorDinLuna(firstName, lastName, monthName);
        rezultat.forEach(p -> {
            Utilizator utilizator = utilizatorService.findOne(p.getLeft());
            System.out.println(utilizator.getLastName() + '|' + utilizator.getFirstName()
                    + '|' + p.getRight().toString());
        });
    }

    public void run_trimite_mesaj(Utilizator utilizatorLogat){
        List<Utilizator> utilizatorList = new ArrayList<>();
        System.out.println("Dati numele destinatarilor: ");
        while (true){
            String first_name = readFirstName();
            String last_name = readLastName();
            Utilizator utilizator = utilizatorService.findByName(first_name, last_name);
            if(utilizator == null) try {
                throw new Exception("Nu exista utilizatorul cu numele si prenumele dat.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            utilizatorList.add(utilizator);

            scanner.nextLine();
            System.out.println("Doriti sa mai adaugati un utilizator la lista de destinatari? (y/n).");
            String optiune = scanner.nextLine();
            if(optiune.equals("n")) break;
        }

        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        Message message = new Message(utilizatorLogat, utilizatorList, mesaj, LocalDateTime.now());
        mesajeService.saveMessage(message);
    }

    public void run_vizualizare_mesaje_primite(Utilizator utilizatorLogat){
        System.out.println(mesajeService.find_all_msg_recived_by_user(utilizatorLogat));
    }

    public void  run_raspunde_la_mesaj(Utilizator utilizatorLogat){
        System.out.println("Dati id-ul mesajului la care doriti sa raspundeti: ");
        Long id = scanner.nextLong();
        Message message = mesajeService.find_one(id);

        scanner.nextLine();
        System.out.println("Mesaj: ");
        String mesaj = scanner.nextLine();

        Message message1 = new Message(utilizatorLogat, Arrays.asList(message.getFrom()), mesaj, LocalDateTime.now());
        mesajeService.saveMessage(message1);
        mesajeService.updateMessage(message);
    }

    public void run_gmail_meniu(){
        boolean ok = true;
        System.out.println("Va rugam sa va autentificati.");
        String first_name_log_in = readFirstName();
        String last_name_log_in = readLastName();
        Utilizator utilizatorLogat = utilizatorService.findByName(first_name_log_in, last_name_log_in);
        if(utilizatorLogat == null){
            System.out.println("Nu exista utilizatorul cu numele si prenumele dat.");
            ok = false;
        }

        while (ok) {
            System.out.println("1. Trimite mesaj.");
            System.out.println("2. Vizualizare mesaje primite.");
            System.out.println("3. Raspunde la un mesaj.");
            System.out.println("b. Back.");

            System.out.println("Optiune = ");
            String optiune = "";
            optiune = scanner.next();
            if (Objects.equals(optiune, "1")) {
                run_trimite_mesaj(utilizatorLogat);
            } else if (Objects.equals(optiune, "2")) {
                run_vizualizare_mesaje_primite(utilizatorLogat);
            } else if (Objects.equals(optiune, "3")) {
                run_raspunde_la_mesaj(utilizatorLogat);
            } else if (Objects.equals(optiune, "b")) {
                break;
            } else {
                System.out.println("Optiune invalida. Reincercati!");
            }
        }
    }

    public void run_console() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. CRUD Utilizator.");
            System.out.println("2. CRUD Prietenie.");
            System.out.println("3. Prietenii utilizator.");
            System.out.println("4. Prietenii utilizator din luna data.");
            System.out.println("5. Log in.");
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
            }else if (Objects.equals(optiune, "5")){
                run_gmail_meniu();
            }else if (Objects.equals(optiune, "x")) {
                return;
            } else {
                System.out.println("Optiune invalida. Reincercati!");
            }
        }
    }
}