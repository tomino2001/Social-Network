package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;
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

public class PrieteniiController {
    public Button btnCancelFrdReq;
    public Button btnAcceptFrdReq;
    public Button btnDeleteFrdReq;
    private GlobalService globalService;
    private Utilizator utilizator;

    public void setService(GlobalService globalService) {
        this.globalService = globalService;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    @FXML
    private TableView<Prietenie> table;
    @FXML
    private TableColumn<Prietenie, String> sentRecivedCol;
    @FXML
    private TableColumn<Prietenie, String> firstNameCol;
    @FXML
    private TableColumn<Prietenie, String> lastNameCol;
    @FXML
    private TableColumn<Prietenie, String> statusCol;
    @FXML
    private TableColumn<Prietenie, LocalDateTime> dateCol;

    private ObservableList<Prietenie> data;

    @FXML
    void initialize() {
        sentRecivedCol.setCellValueFactory(cellData -> {
            if (Objects.equals(cellData.getValue().getId().getLeft(), utilizator.getId()))
                return new SimpleStringProperty("Sent to");
            return new SimpleStringProperty("Recived from");
        });
        firstNameCol.setCellValueFactory(cellData -> {
            if (!Objects.equals(cellData.getValue().getId().getLeft(), utilizator.getId()))
                return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
                        cellData.getValue().getId().getLeft()).getFirstName());
            return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
                    cellData.getValue().getId().getRight()).getFirstName());
        });
        lastNameCol.setCellValueFactory(cellData -> {
            if (!Objects.equals(cellData.getValue().getId().getLeft(), utilizator.getId()))
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
        Prietenie prietenieSelectata = table.getSelectionModel().getSelectedItem();
        if (prietenieSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat o prietenie!");
            return;
        }
        if (Objects.equals(prietenieSelectata.getStatus(), "approved")) {
            alertMessage(Alert.AlertType.WARNING, "Ati acceptat deja prietenia selectata!");
            return;
        }
        if (prietenieSelectata.getId().getLeft().equals(utilizator.getId())) {
            alertMessage(Alert.AlertType.ERROR, "Action denied");
            return;
        }
        data.remove(prietenieSelectata);
        prietenieSelectata.setStatus("approved");
        globalService.getPrietenieService().updatePrietenie(prietenieSelectata);
        Prietenie prietenieAux = new Prietenie
                (prietenieSelectata.getId().getLeft(), prietenieSelectata.getId().getRight());
        prietenieAux.setStatus("approved");
        prietenieAux.setDate(LocalDateTime.now());
        data.add(prietenieAux);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void onBtnCancelFriendReqClicked() {
        Prietenie prietenieSelectata = table.getSelectionModel().getSelectedItem();
        if (prietenieSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat o prietenie!");
            return;
        }
        if (Objects.equals(prietenieSelectata.getStatus(), "approved")) {
            alertMessage(Alert.AlertType.WARNING, "Prietenia a fost deja acceptata!");
            return;
        }
        globalService.getPrietenieService().removePrietenie(prietenieSelectata.getId().getLeft(), prietenieSelectata.getId().getRight());
        data.remove(prietenieSelectata);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void onBtnDeleteFriendReqClicked() {
        Prietenie prietenieSelectata = table.getSelectionModel().getSelectedItem();
        if (prietenieSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat o prietenie!");
            return;
        }
        globalService.getPrietenieService().removePrietenie(prietenieSelectata.getId().getLeft(), prietenieSelectata.getId().getRight());
        data.remove(prietenieSelectata);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void setAll(GlobalService globalService, Utilizator utilizator, ObservableList<Prietenie> data) {
        setService(globalService);
        setUtilizator(utilizator);
        setObservableList(data);
        table.setItems(data);
    }

    private void setObservableList(ObservableList<Prietenie> data) {
        this.data = data;
    }
}