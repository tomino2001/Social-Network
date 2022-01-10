package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.GlobalService;
import com.example.socialnetwork.service.HashPasswordService;
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
            User user = globalService.getUtilizatorService().findOne(account.getId());
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.close();
            enterAccount(user);
        }
    }

    private void enterAccount(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/accountView.fxml"));
        Parent parent = loader.load();
        AccountController accountController = loader.getController();
        accountController.initAccount(user, globalService);

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(user.getFirstName() + ' ' + user.getLastName());
        stage.setScene(scene);
        stage.show();
    }

    public void setService(GlobalService globalService) {
        LoginController.globalService = globalService;
    }

    public void onBtnRegisterClicked() throws IOException {
        Stage accountStage = (Stage) btnRegister.getScene().getWindow();
        accountStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/registerView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        RegisterController registerController = fxmlLoader.getController();
        registerController.setService(globalService);

        Stage stage = new Stage();
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
}