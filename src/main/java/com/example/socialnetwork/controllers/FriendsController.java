package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsController {
    public Button btnShowAllFrdReq;
    public Button btnAddFriend;
    public Button btnRemoveFriend;
    public Button btnSendMessage;
    public Button btnRefresh;


    private GlobalService globalService;
    private User user;


    public FriendsController() {
    }

    private final ObservableList<Friendship> data = FXCollections.observableArrayList();

    @FXML
    private TableView<Friendship> tableView;

    @FXML
    private TableColumn<Friendship, String> columnFirstName;

    @FXML
    private TableColumn<Friendship, String> columnLastName;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextArea txtMessage;

    @FXML
    private void initialize() {
        columnFirstName.setCellValueFactory(cellData -> extractFriendLastName(cellData, true));
        columnLastName.setCellValueFactory(cellData -> extractFriendLastName(cellData, false));
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public SimpleStringProperty extractFriendLastName(TableColumn.CellDataFeatures<Friendship, String> cellData, boolean extractFirst) {
        if (!Objects.equals(cellData.getValue().getStatus(), "approved"))
            return new SimpleStringProperty("-----");
        User prieten;
        Long left = cellData.getValue().getId().getLeft();
        Long right = cellData.getValue().getId().getRight();
        if (left.equals(user.getId()))
            prieten = globalService.getUtilizatorService().findOne(right);
        else
            prieten = globalService.getUtilizatorService().findOne(left);
        if (extractFirst)
            return new SimpleStringProperty(prieten.getFirstName());
        else
            return new SimpleStringProperty(prieten.getLastName());
    }

    public void initController(User user, GlobalService globalService) {
        this.globalService = globalService;
        this.user = user;
        data.addAll(this.globalService.listaPrieteniUtilizator(user.getFirstName(), user.getLastName()));
        tableView.setItems(data);
    }

    @FXML
    public void onFriendReqButtonClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/friendsManagementView.fxml"));
        Parent parent = loader.load();

        data.clear();
        data.addAll(this.globalService.listaPrieteniUtilizator(user.getFirstName(), user.getLastName()));
        tableView.setItems(data);

        FriendsManagementController friendsManagementController = loader.getController();
        friendsManagementController.setAll(globalService, user, data);

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(user.getFirstName() + ' ' + user.getLastName() + " - Friendship requestes");
        stage.setScene(scene);
        stage.show();
    }

    public void onBtnAddFriendClicked() {
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        User prieten = globalService.getUtilizatorService().findByName(firstName, lastName);
        if (prieten == null) {
            alertMessage(Alert.AlertType.WARNING, "User does not exist");
            return;
        }
        Friendship friendship = new Friendship(user.getId(), prieten.getId(), LocalDateTime.now());
        friendship.setStatus("pending");
        if (globalService.getPrietenieService().addPrietenie(friendship) == null)
            data.add(friendship);
        alertMessage(Alert.AlertType.CONFIRMATION, "Success!");
    }

    private void alertMessage(Alert.AlertType tipAlerta, String mesaj) {
        Alert alert = new Alert(tipAlerta, mesaj);
        alert.show();
    }

    public void onBtnRemoveFriendClicked() {
        Friendship friendshipSelectata = tableView.getSelectionModel().getSelectedItem();
        if (friendshipSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "First select an user");
            return;
        }
        globalService.getPrietenieService().removePrietenie(
                friendshipSelectata.getId().getLeft(), friendshipSelectata.getId().getRight());
        globalService.getPrietenieService().removePrietenie(
                friendshipSelectata.getId().getRight(), friendshipSelectata.getId().getLeft());

        data.remove(friendshipSelectata);
        alertMessage(Alert.AlertType.CONFIRMATION, "Success!");
    }

    public void onBtnSendMessageClicked() {
        List<Friendship> friendshipList = tableView.getSelectionModel().getSelectedItems();
        List<User> userList = new ArrayList<>();
        String message = txtMessage.getText();

        for (Friendship friendship : friendshipList) {
            if(friendship.getId().getLeft().equals(user.getId()))
                userList.add(globalService.getUtilizatorService().findOne(friendship.getId().getRight()));
            else
                userList.add(globalService.getUtilizatorService().findOne(friendship.getId().getLeft()));
        }
        Message message1 = new Message(user, userList, message, LocalDateTime.now());
        globalService.getMesajeService().saveMessage(message1);
        alertMessage(Alert.AlertType.CONFIRMATION, "Success!");
    }

    public void onBtnRefreshClicked() {
        data.clear();
        data.addAll(this.globalService.listaPrieteniUtilizator(user.getFirstName(), user.getLastName()));
        tableView.setItems(data);
    }
}