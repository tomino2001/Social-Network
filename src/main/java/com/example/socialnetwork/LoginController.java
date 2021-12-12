package com.example.socialnetwork;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.Constants;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.db.UtilizatorDbRepository;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;

public class LoginController {

    private UtilizatorValidator utilizatorValidator = new UtilizatorValidator();
    private Repository<Long, Utilizator> utilizatorRepository =
            new UtilizatorDbRepository(Constants.url, Constants.username, Constants.password, utilizatorValidator);
    private UtilizatorService utilizatorService = new UtilizatorService(utilizatorRepository);

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
        Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
        if (utilizator == null)
            labelUserInexistent.setText("User does not exist!");
        else {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.close();
            enterAccount(utilizator);
        }
    }

    private void enterAccount(Utilizator utilizator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("accountView.fxml"));
        Scene scene = new Scene(loader.load());
        AccountController accountController = loader.getController();
        accountController.setUtilizator(utilizator);

        Stage stage = new Stage();
        stage.setTitle(utilizator.getFirstName() + ' ' + utilizator.getLastName());
        stage.setScene(scene);
        stage.show();
    }
}