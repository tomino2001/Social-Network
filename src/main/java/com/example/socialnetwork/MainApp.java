package com.example.socialnetwork;

import com.example.socialnetwork.controller.LoginController;
import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.domain.validators.PrietenieValidator;
import com.example.socialnetwork.domain.validators.UtilizatorValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.MessagesDbRepository;
import com.example.socialnetwork.repository.db.PrieteniiDbRepository;
import com.example.socialnetwork.repository.db.UtilizatorDbRepository;
import com.example.socialnetwork.service.GlobalService;
import com.example.socialnetwork.service.MesajeService;
import com.example.socialnetwork.service.PrietenieService;
import com.example.socialnetwork.service.UtilizatorService;
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

    private GlobalService constructGlobalService(){
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

        GlobalService globalService = new
                GlobalService(utilizatorService, prietenieService, mesajeService);
        return globalService;
    }
}