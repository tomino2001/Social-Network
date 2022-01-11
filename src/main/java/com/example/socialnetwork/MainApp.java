package com.example.socialnetwork;

import com.example.socialnetwork.controllers.LoginController;
import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.domain.validators.FriendshipValidator;
import com.example.socialnetwork.domain.validators.UserValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.*;
import com.example.socialnetwork.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GlobalService globalService = constructGlobalService();

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL resource = getClass().getResource("/com/example/socialnetwork/loginView.fxml");
        fxmlLoader.setLocation(resource);
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(globalService);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private GlobalService constructGlobalService() {
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

        Repository<Long, Event> eventRepository =
                new EventDbRepository(Constants.url, Constants.username, Constants.password);
        EventService eventService = new EventService(eventRepository);

        Repository<Long, Notification> notificationRepository =
                new NotificationDbRepository(Constants.url, Constants.username, Constants.password);
        NotificationService notificationService = new NotificationService(notificationRepository);

        return new
                GlobalService(usersService, friendshipsService, messagesService, accountsService, eventService, notificationService);
    }
}