package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendsManagementController {
    public Button btnCancelFrdReq;
    public Button btnAcceptFrdReq;
    public Button btnDeleteFrdReq;
    private GlobalService globalService;
    private User user;
    @FXML
    private TableView<Friendship> table;
    @FXML
    private TableColumn<Friendship, String> sentReceivedCol;
    @FXML
    private TableColumn<Friendship, String> firstNameCol;
    @FXML
    private TableColumn<Friendship, String> lastNameCol;
    @FXML
    private TableColumn<Friendship, String> statusCol;
    @FXML
    private TableColumn<Friendship, LocalDateTime> dateCol;
    private ObservableList<Friendship> data;

    @FXML
    void initialize() {
        sentReceivedCol.setCellValueFactory(cellData -> {
            if (Objects.equals(cellData.getValue().getId().getLeft(), user.getId()))
                return new SimpleStringProperty("Sent to");
            return new SimpleStringProperty("Received from");
        });
        firstNameCol.setCellValueFactory(cellData -> {
            if (!Objects.equals(cellData.getValue().getId().getLeft(), user.getId()))
                return new SimpleStringProperty(globalService.getUserService().findOne(
                        cellData.getValue().getId().getLeft()).getFirstName());
            return new SimpleStringProperty(globalService.getUserService().findOne(
                    cellData.getValue().getId().getRight()).getFirstName());
        });
        lastNameCol.setCellValueFactory(cellData -> {
            if (!Objects.equals(cellData.getValue().getId().getLeft(), user.getId()))
                return new SimpleStringProperty(globalService.getUserService().findOne(
                        cellData.getValue().getId().getLeft()).getLastName());
            return new SimpleStringProperty(globalService.getUserService().findOne(
                    cellData.getValue().getId().getRight()).getLastName());
        });
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
    }

    private void alertMessage(Alert.AlertType tipAlerta, String mesaj) {
        Alert alert = new Alert(tipAlerta, mesaj);
        alert.show();
    }

    public void onBtnAcceptFriendReqClicked() {
        Friendship selectedFriendship = table.getSelectionModel().getSelectedItem();
        if (selectedFriendship == null) {
            alertMessage(Alert.AlertType.ERROR, "Select a friendship!");
            return;
        }
        if (Objects.equals(selectedFriendship.getStatus(), "approved")) {
            alertMessage(Alert.AlertType.WARNING, "Friendship already accepted!");
            return;
        }
        if (selectedFriendship.getId().getLeft().equals(user.getId())) {
            alertMessage(Alert.AlertType.ERROR, "Action denied");
            return;
        }
        data.remove(selectedFriendship);
        selectedFriendship.setStatus("approved");
        globalService.getFriendshipService().updateFriendship(selectedFriendship);
        Friendship friendshipAux = new Friendship
                (selectedFriendship.getId().getLeft(), selectedFriendship.getId().getRight());
        friendshipAux.setStatus("approved");
        friendshipAux.setDate(LocalDateTime.now());
        data.add(friendshipAux);
    }

    public void onBtnCancelFriendReqClicked() {
        Friendship selectedFriendship = table.getSelectionModel().getSelectedItem();
        if (selectedFriendship == null) {
            alertMessage(Alert.AlertType.ERROR, "Select a friendship!");
            return;
        }
        if (Objects.equals(selectedFriendship.getStatus(), "approved")) {
            alertMessage(Alert.AlertType.WARNING, "Friendship already accepted!");
            return;
        }
        globalService.getFriendshipService().removeFriendship(selectedFriendship.getId().getLeft(), selectedFriendship.getId().getRight());
        data.remove(selectedFriendship);
    }

    public void onBtnDeleteFriendReqClicked() {
        Friendship selectedFriendship = table.getSelectionModel().getSelectedItem();
        if (selectedFriendship == null) {
            alertMessage(Alert.AlertType.ERROR, "Select a friendship!");
            return;
        }
        globalService.getFriendshipService().removeFriendship(selectedFriendship.getId().getLeft(), selectedFriendship.getId().getRight());
        data.remove(selectedFriendship);
    }

    public void setAll(GlobalService globalService, User user, ObservableList<Friendship> data) {
        this.globalService = globalService;
        this.user = user;
        this.data = data;
        table.setItems(data);
    }
}