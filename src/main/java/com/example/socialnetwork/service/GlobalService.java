package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

import java.time.Month;

public class GlobalService {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;
    private final MesajeService mesajeService;

    public UtilizatorService getUtilizatorService() {
        return utilizatorService;
    }

    public PrietenieService getPrietenieService() {
        return prietenieService;
    }

    public MesajeService getMesajeService() {
        return mesajeService;
    }

    public GlobalService(UtilizatorService utilizatorService, PrietenieService prietenieService, MesajeService mesajeService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.mesajeService = mesajeService;
    }

    public void removeUtilizatorAndPrieteniiUtilizator(Long id) {
        this.utilizatorService.removeUtilizator(id);
        this.prietenieService.removePreteniiIfUserIsDeleted(id);
    }

    public List<Tuple<Long, LocalDateTime>> prieteniiUtilizator(String firstName, String lastName) {
        Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
        Iterable<Prietenie> prietenii = prietenieService.getAll();
        List<Tuple<Long, LocalDateTime>> rezultat =
                StreamSupport.stream(prietenii.spliterator(), false)
                        .filter(prietenie -> ((prietenie.getId().getLeft() == utilizator.getId()
                                || Objects.equals(prietenie.getId().getRight(), utilizator.getId()))
                                && prietenie.getStatus().equals("approved")))
                        .map(prietenie -> mapPrietenie(utilizator, prietenie))
                        .collect(toList());
        return rezultat;
    }

    public List<Utilizator> utilizatoriPrieteniCuUtilizator(String firstName, String lastName) {
        return prieteniiUtilizator(firstName, lastName)
                .stream()
                .map(x -> {
                    Utilizator utilizator = utilizatorService.findOne(x.getLeft());
                    return utilizator;
                })
                .collect(toList());
    }

    public List<Tuple<Long, LocalDateTime>> prieteniiUtilizatorDinLuna(String firstName, String lastName, String luna) {
        Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
        Iterable<Prietenie> prietenii = prietenieService.getAll();

        List<Tuple<Long, LocalDateTime>> rezultat =
                StreamSupport.stream(prietenii.spliterator(), false)
                        .filter(prietenie -> (prietenie.getId().getLeft() == utilizator.getId()
                                || Objects.equals(prietenie.getId().getRight(), utilizator.getId()))
                                && prietenie.getDate().getMonth() == Month.valueOf(luna.toUpperCase()))
                        .map(prietenie -> mapPrietenie(utilizator, prietenie))
                        .collect(toList());
        return rezultat;
    }

    private Tuple<Long, LocalDateTime> mapPrietenie(Utilizator utilizator, Prietenie prietenie) {
        Long left = prietenie.getId().getLeft();
        Long right = prietenie.getId().getRight();
        Long idUser = utilizator.getId();
        Tuple<Long, LocalDateTime> tuple = null;
        if (left != idUser)
            tuple = new Tuple<>(left, prietenie.getDate());
        else
            tuple = new Tuple<>(right, prietenie.getDate());
        return tuple;
    }
}