package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.AccountService;
import com.example.socialnetwork.service.GlobalService;
import com.example.socialnetwork.service.HashPasswordService;
import javafx.event.ActionEvent;
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
    private static final HashPasswordService hashPasswordService = new HashPasswordService();
    public Button btnRegister;

    @FXML
    private Button btnLogin;

    @FXML
    private Label labelUserInexistent;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private TextField textFieldPassword;

    @FXML
    public void onLoginButtonClicked() throws IOException {
        String username = textFieldUsername.getText();
        String password = hashPasswordService.hashPassword(textFieldPassword.getText());
        Account account = globalService.getAccountService().getAccountByUsernameAndPassword(username, password);
        if (account == null)
            labelUserInexistent.setText("Username or password wrong!");
        else {
            Utilizator utilizator = globalService.getUtilizatorService().findOne(account.getId());
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

    public void onBtnRegisterClicked() {

    }
}