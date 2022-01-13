package com.example.socialnetwork;

import com.example.socialnetwork.controllers.LoginController;
import com.example.socialnetwork.service.GlobalService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        GlobalService globalService = GlobalService.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL resource = getClass().getResource("/com/example/socialnetwork/loginView.fxml");
        fxmlLoader.setLocation(resource);

        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(globalService);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}