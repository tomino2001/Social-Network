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

    public void setService(GlobalService globalService) {
        this.globalService = globalService;
    }

    public void setUtilizator(User user) {
        this.user = user;
    }

    @FXML
    private TableView<Friendship> table;
    @FXML
    private TableColumn<Friendship, String> sentRecivedCol;
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
        sentRecivedCol.setCellValueFactory(cellData -> {
            if (Objects.equals(cellData.getValue().getId().getLeft(), user.getId()))
                return new SimpleStringProperty("Sent to");
            return new SimpleStringProperty("Recived from");
        });
        firstNameCol.setCellValueFactory(cellData -> {
            if (!Objects.equals(cellData.getValue().getId().getLeft(), user.getId()))
                return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
                        cellData.getValue().getId().getLeft()).getFirstName());
            return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
                    cellData.getValue().getId().getRight()).getFirstName());
        });
        lastNameCol.setCellValueFactory(cellData -> {
            if (!Objects.equals(cellData.getValue().getId().getLeft(), user.getId()))
                return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
                        cellData.getValue().getId().getLeft()).getLastName());
            return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
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
        Friendship friendshipSelectata = table.getSelectionModel().getSelectedItem();
        if (friendshipSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat o prietenie!");
            return;
        }
        if (Objects.equals(friendshipSelectata.getStatus(), "approved")) {
            alertMessage(Alert.AlertType.WARNING, "Ati acceptat deja prietenia selectata!");
            return;
        }
        if (friendshipSelectata.getId().getLeft().equals(user.getId())) {
            alertMessage(Alert.AlertType.ERROR, "Action denied");
            return;
        }
        data.remove(friendshipSelectata);
        friendshipSelectata.setStatus("approved");
        globalService.getPrietenieService().updatePrietenie(friendshipSelectata);
        Friendship friendshipAux = new Friendship
                (friendshipSelectata.getId().getLeft(), friendshipSelectata.getId().getRight());
        friendshipAux.setStatus("approved");
        friendshipAux.setDate(LocalDateTime.now());
        data.add(friendshipAux);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void onBtnCancelFriendReqClicked() {
        Friendship friendshipSelectata = table.getSelectionModel().getSelectedItem();
        if (friendshipSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat o prietenie!");
            return;
        }
        if (Objects.equals(friendshipSelectata.getStatus(), "approved")) {
            alertMessage(Alert.AlertType.WARNING, "Prietenia a fost deja acceptata!");
            return;
        }
        globalService.getPrietenieService().removePrietenie(friendshipSelectata.getId().getLeft(), friendshipSelectata.getId().getRight());
        data.remove(friendshipSelectata);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void onBtnDeleteFriendReqClicked() {
        Friendship friendshipSelectata = table.getSelectionModel().getSelectedItem();
        if (friendshipSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat o prietenie!");
            return;
        }
        globalService.getPrietenieService().removePrietenie(friendshipSelectata.getId().getLeft(), friendshipSelectata.getId().getRight());
        data.remove(friendshipSelectata);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void setAll(GlobalService globalService, User user, ObservableList<Friendship> data) {
        setService(globalService);
        setUtilizator(user);
        setObservableList(data);
        table.setItems(data);
    }

    private void setObservableList(ObservableList<Friendship> data) {
        this.data = data;
    }
}