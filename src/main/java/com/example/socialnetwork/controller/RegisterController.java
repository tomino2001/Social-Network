package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.GlobalService;
import com.example.socialnetwork.service.HashPasswordService;
import com.example.socialnetwork.service.UtilizatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    private static GlobalService globalService;
    private static final HashPasswordService hashPasswordService = new HashPasswordService();
    public Button btnCreateAccount;
    @FXML
    private TextField txtFieldFirstName;
    @FXML
    private TextField txtFieldLastName;
    @FXML
    private TextField txtFieldUsername;
    @FXML
    private PasswordField txtFieldPassword;

    public void setService(GlobalService globalService) {
        RegisterController.globalService = globalService;
    }

    public void onBtnCreateAccountClicked() throws IOException {
        String firstName = txtFieldFirstName.getText();
        String lastName = txtFieldLastName.getText();
        String username = txtFieldUsername.getText();
        String password = hashPasswordService.hashPassword(txtFieldPassword.getText());

        Utilizator utilizator = new Utilizator(firstName, lastName);
        utilizator.setId(1000L);
        globalService.getUtilizatorService().addUtilizator(utilizator);
        Account account = new Account(username, password);
        account.setId(1000L);
        globalService.getAccountService().addUAccount(account);

        Stage accountStage = (Stage) btnCreateAccount.getScene().getWindow();
        accountStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(globalService);

        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
