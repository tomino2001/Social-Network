package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.GlobalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountController {

    public Label nameLabel;
    public AnchorPane currentPane;
    public HBox mainHBox;
    public Button friendsBtn;
    public Button chatBtn;
    public Button eventsBtn;
    public ImageView logoutImg;
    private GlobalService globalService;
    private User user;

    @FXML
    public void onChatBtnClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/chatView.fxml"));
        AnchorPane chatPane = loader.load();
        MessagesController messagesController = loader.getController();
        messagesController.setAll(globalService, user);
        mainHBox.getChildren().set(1, chatPane);
    }

    @FXML
    public void onFriendsBtnClicked() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/friendsView.fxml"));
        AnchorPane friendsPane = fxmlLoader.load();
        FriendsController friendsController = fxmlLoader.getController();
        friendsController.setAll(globalService, user);
        mainHBox.getChildren().set(1, friendsPane);
    }

    @FXML
    public void onEventsBtnClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/eventsView.fxml"));
        AnchorPane eventsPane = loader.load();
        EventsController eventsController = loader.getController();
        eventsController.setAll(globalService, user);
        mainHBox.getChildren().set(1, eventsPane);
    }

    public void initAccount(User user, GlobalService globalService) {
        this.globalService = globalService;
        this.user = user;
        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
    }

    public void onLogoutImgPress() throws IOException {
        Stage accountStage = (Stage) logoutImg.getScene().getWindow();
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