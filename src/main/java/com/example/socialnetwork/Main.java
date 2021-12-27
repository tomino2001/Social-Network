package com.example.socialnetwork;

import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.domain.validators.PrietenieValidator;
import com.example.socialnetwork.domain.validators.UtilizatorValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.AccountDbRepository;
import com.example.socialnetwork.repository.db.MessagesDbRepository;
import com.example.socialnetwork.repository.db.PrieteniiDbRepository;
import com.example.socialnetwork.repository.db.UtilizatorDbRepository;
import com.example.socialnetwork.service.*;
import com.example.socialnetwork.ui.Console;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.Utilizator;


public class Main {
    public static void main(String[] args) {

        Validator<Utilizator> validatorUtilizator = new UtilizatorValidator();
        Validator<Prietenie> validatorPrietenie = new PrietenieValidator();

        Repository<Long, Utilizator> utilizatorDbRepository =
                new UtilizatorDbRepository(Constants.url, Constants.username, Constants.password, validatorUtilizator);
        UtilizatorService utilizatorService = new UtilizatorService(utilizatorDbRepository);

        Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository =
                new PrieteniiDbRepository(Constants.url, Constants.username, Constants.password, validatorPrietenie);
        PrietenieService prietenieService = new PrietenieService(prietenieDbRepository);

        Repository<Long, Message> mesajeRepository =
                new MessagesDbRepository(Constants.url, Constants.username, Constants.password, null);
        MesajeService mesajeService = new MesajeService(mesajeRepository);

        Repository<Long, Account> accountRepository =
                new AccountDbRepository(Constants.url, Constants.username, Constants.password, null);
        AccountService accountService = new AccountService(accountRepository);

        GlobalService globalService = new GlobalService(utilizatorService, prietenieService, mesajeService, accountService);
        Console console = new Console(utilizatorService, prietenieService, globalService, mesajeService);
        console.run_console();
    }
}