package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.GlobalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountController {

    public Label nameLabel;
    public AnchorPane currentPane;
    public HBox mainHBox;
    public Button logoutBtn;
    private GlobalService globalService;
    private User user;

    @FXML
    void onChatBtnClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/chatView.fxml"));
        AnchorPane chatPane = loader.load();
        MessageController messageController = loader.getController();
        messageController.setAll(globalService, user);
        mainHBox.getChildren().set(1, chatPane);
    }

    @FXML
    void onFriendsBtnClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/friendsView.fxml"));
        AnchorPane friendsPane = fxmlLoader.load();
        FriendsController friendsController = fxmlLoader.getController();
        friendsController.initController(user, globalService);
        mainHBox.getChildren().set(1, friendsPane);
    }

    public void initAccount(User user, GlobalService globalService) {
        this.globalService = globalService;
        this.user = user;
        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
    }

    public void onLogoutBtnClicked() throws IOException {
        Stage accountStage = (Stage) logoutBtn.getScene().getWindow();
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