package socialnetwork.service;

import jdk.vm.ci.meta.Local;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class UtilizatoriPrieteniiService {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;

    public UtilizatoriPrieteniiService(UtilizatorService utilizatorService, PrietenieService prietenieService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
    }

    public void removeUtilizatorAndPrieteniiUtilizator(Long id){
        this.utilizatorService.removeUtilizator(id);
        this.prietenieService.removePreteniiIfUserIsDeleted(id);
    }

    public List<Tuple<Long, LocalDateTime>> prieteniiUtilizatorDinLuna(String firstName, String lastName, String luna){
        Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
        Iterable<Prietenie> prietenii = prietenieService.getAll();

        List<Tuple<Long, LocalDateTime>> rezultat =
                StreamSupport.stream(prietenii.spliterator(), false)
                .filter(prietenie -> (prietenie.getId().getLeft() == utilizator.getId()
                                || Objects.equals(prietenie.getId().getRight(), utilizator.getId()))
                            && prietenie.getDate().getMonth() == Month.valueOf(luna.toUpperCase()))
                .map(prietenie -> {
                    Long left = prietenie.getId().getLeft();
                    Long right = prietenie.getId().getRight();
                    Long idUser = utilizator.getId();
                    Tuple<Long, LocalDateTime> tuple = null;
                    if (left != idUser)
                        tuple = new Tuple<>(left, prietenie.getDate());
                    else
                        tuple = new Tuple<>(right, prietenie.getDate());
                    return tuple;
                })
                .collect(toList());
        return rezultat;
    }
}
