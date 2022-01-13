package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.exceptions.ValidationException;
import com.example.socialnetwork.service.GlobalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    private static GlobalService globalService;
    public Button btnCreateAccount;
    public ImageView backImg;
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
        String password = globalService.getAccountService().hashPassword(txtFieldPassword.getText());

        User user = new User(firstName, lastName);
        user.setId(1000L);
        try {
            globalService.getUserService().addUser(user);
            Account account = new Account(username, password);
            account.setId(1000L);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Success");
            alert.show();
            globalService.getAccountService().addUAccount(account);
            
        } catch (ValidationException ve) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ve.getMessage());
            alert.show();
        }

    }

    public void onBackImgPress(MouseEvent mouseEvent) throws IOException {
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
