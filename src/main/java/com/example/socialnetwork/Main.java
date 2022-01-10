package com.example.socialnetwork;

import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.domain.validators.FriendshipValidator;
import com.example.socialnetwork.domain.validators.UserValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.AccountsDbRepository;
import com.example.socialnetwork.repository.db.MessagesDbRepository;
import com.example.socialnetwork.repository.db.FriendshipsDbRepository;
import com.example.socialnetwork.repository.db.UsersDbRepository;
import com.example.socialnetwork.service.*;
import com.example.socialnetwork.ui.Console;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.User;


public class Main {
    public static void main(String[] args) {

        Validator<User> validatorUtilizator = new UserValidator();
        Validator<Friendship> validatorPrietenie = new FriendshipValidator();

        Repository<Long, User> utilizatorDbRepository =
                new UsersDbRepository(Constants.url, Constants.username, Constants.password, validatorUtilizator);
        UsersService usersService = new UsersService(utilizatorDbRepository);

        Repository<Tuple<Long, Long>, Friendship> prietenieDbRepository =
                new FriendshipsDbRepository(Constants.url, Constants.username, Constants.password, validatorPrietenie);
        FriendshipsService friendshipsService = new FriendshipsService(prietenieDbRepository);

        Repository<Long, Message> mesajeRepository =
                new MessagesDbRepository(Constants.url, Constants.username, Constants.password, null);
        MessagesService messagesService = new MessagesService(mesajeRepository);

        Repository<Long, Account> accountRepository =
                new AccountsDbRepository(Constants.url, Constants.username, Constants.password, null);
        AccountsService accountsService = new AccountsService(accountRepository);

        GlobalService globalService = new GlobalService(usersService, friendshipsService, messagesService, accountsService);
        Console console = new Console(usersService, friendshipsService, globalService, messagesService);
        console.run_console();
    }
}