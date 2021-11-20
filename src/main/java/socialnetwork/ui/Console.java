package socialnetwork.ui;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatoriPrieteniiService;
import socialnetwork.service.UtilizatorService;

import java.security.KeyException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Scanner;

public class Console {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;
    private final UtilizatoriPrieteniiService utilizatoriPrieteniiService;

    public Console(UtilizatorService utilizatorService, PrietenieService prietenieService,
                   UtilizatoriPrieteniiService utilizatoriPrieteniiService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.utilizatoriPrieteniiService = utilizatoriPrieteniiService;
    }

    public void run_meniu_CRUD_Utilizator() {
        Scanner scanner = new Scanner(System.in);
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
                    System.out.println("Id: ");
                    Long id = scanner.nextLong();
                    System.out.println("Frist name: ");
                    String firstName = scanner.next();
                    System.out.println("Last name: ");
                    String lastName = scanner.next();
                    Utilizator utilizator = new Utilizator(firstName, lastName);
                    utilizator.setId(id);
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
                    System.out.println("Frist name: ");
                    String firstName = scanner.next();
                    System.out.println("Last name: ");
                    String lastName = scanner.next();
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
            }  else if (Objects.equals(optiune, "5")) {
                System.out.println("Frist name: ");
                String firstName = scanner.next();
                System.out.println("Last name: ");
                String lastName = scanner.next();
                Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
                if (utilizator != null)
                    System.out.println(utilizator);
                else
                    System.out.println("Doesn't exist!");
            }
            else if (Objects.equals(optiune, "b"))
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
                    System.out.println("Id1: ");
                    Long id1 = scanner.nextLong();
                    Utilizator utilizator = utilizatorService.findOne(id1);
                    if (utilizator == null) throw new KeyException("Nu exista utilizatorul cu id-ul dat");
                    System.out.println("Id2: ");
                    Long id2 = scanner.nextLong();
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
                    System.out.println("Id1: ");
                    Long id1 = scanner.nextLong();
                    System.out.println("Id2: ");
                    Long id2 = scanner.nextLong();
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

    public void run_console() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. CRUD Utilizator.");
            System.out.println("2. CRUD Prietenie.");
            System.out.println("x. Exit.");

            System.out.println("Optiune = ");
            String optiune = "";
            optiune = scanner.next();
            if (Objects.equals(optiune, "1")) {
                run_meniu_CRUD_Utilizator();
            } else if (Objects.equals(optiune, "2")) {
                run_meniu_CRUD_Prietenie();
            } else if (Objects.equals(optiune, "x")) {
                return;
            } else {
                System.out.println("Optiune invalida. Reincercati!");
            }
        }
    }
}