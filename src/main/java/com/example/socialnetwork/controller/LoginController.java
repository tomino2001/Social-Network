package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.GlobalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private static GlobalService globalService;

    @FXML
    private Button btnLogin;

    @FXML
    private Label labelUserInexistent;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldLastName;

    @FXML
    public void onLoginButtonClicked() throws IOException {
        labelUserInexistent.setText("");
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        Utilizator utilizator = globalService.getUtilizatorService().findByName(firstName, lastName);
        if (utilizator == null)
            labelUserInexistent.setText("User does not exist!");
        else {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.close();
            enterAccount(utilizator);
        }
    }

    private void enterAccount(Utilizator utilizator) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/accountView.fxml"));
        Parent parent = loader.load();
        AccountController accountController = loader.getController();
        accountController.initAccount(utilizator, globalService);

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(utilizator.getFirstName() + ' ' + utilizator.getLastName());
        stage.setScene(scene);
        stage.show();
    }

    public void setService(GlobalService globalService) {
        LoginController.globalService = globalService;
    }
}