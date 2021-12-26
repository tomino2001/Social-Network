package com.example.socialnetwork.controller;

import com.example.socialnetwork.MainApp;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AccountController {
    public Button btnShowAllFrdReq;
    public Button btnAddFriend;
    public Button btnRemoveFriend;

    private GlobalService globalService;
    private Utilizator utilizator;

    public AccountController() {
    }

    private final ObservableList<Prietenie> data = FXCollections.observableArrayList();

    @FXML
    private TableView<Prietenie> tableView;

    @FXML
    private TableColumn<Prietenie, String> columnFirstName;

    @FXML
    private TableColumn<Prietenie, String> columnLastName;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private void initialize() {
        columnFirstName.setCellValueFactory(cellData -> extractFriendLastName(cellData, true));
        columnLastName.setCellValueFactory(cellData -> extractFriendLastName(cellData, false));
    }

    public SimpleStringProperty extractFriendLastName(TableColumn.CellDataFeatures<Prietenie, String> cellData, boolean extractFirst) {
        if (!Objects.equals(cellData.getValue().getStatus(), "approved"))
            return null;
        Utilizator prieten;
        Long left = cellData.getValue().getId().getLeft();
        Long right = cellData.getValue().getId().getRight();
        if (left.equals(utilizator.getId()))
            prieten = globalService.getUtilizatorService().findOne(right);
        else
            prieten = globalService.getUtilizatorService().findOne(left);
        if (extractFirst)
            return new SimpleStringProperty(prieten.getFirstName());
        else
            return new SimpleStringProperty(prieten.getLastName());
    }

    public void initAccount(Utilizator utilizator) {
        this.utilizator = utilizator;
        data.addAll(globalService.listaPrieteniUtilizator(utilizator.getFirstName(), utilizator.getLastName()));
        tableView.setItems(data);
    }

    @FXML
    public void onFriendReqButtonClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/prieteniiView.fxml"));
        Parent parent = loader.load();
        PrieteniiController prieteniiController = loader.getController();
        prieteniiController.setAll(globalService, utilizator, data);

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(utilizator.getFirstName() + ' ' + utilizator.getLastName() + " - Friendship requestes");
        stage.setScene(scene);
        stage.show();
    }

    public void onBtnAddFriendClicked() {
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        Utilizator prieten = globalService.getUtilizatorService().findByName(firstName, lastName);
        if (prieten == null) {
            alertMessage(Alert.AlertType.WARNING, "Utilizatorul nu exista");
            return;
        }
        Prietenie prietenie = new Prietenie(utilizator.getId(), prieten.getId(), LocalDateTime.now());
        prietenie.setStatus("pending");
        if (globalService.getPrietenieService().addPrietenie(prietenie) == null)
            data.add(prietenie);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    private void alertMessage(Alert.AlertType tipAlerta, String mesaj) {
        Alert alert = new Alert(tipAlerta, mesaj);
        alert.show();
    }

    public void onBtnRemoveFriendClicked() {
        Prietenie prietenieSelectata = tableView.getSelectionModel().getSelectedItem();
        if (prietenieSelectata == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat un u" +
                    "tilizator!");
            return;
        }
        globalService.getPrietenieService().removePrietenie(
                prietenieSelectata.getId().getLeft(), prietenieSelectata.getId().getRight());
        globalService.getPrietenieService().removePrietenie(
                prietenieSelectata.getId().getRight(), prietenieSelectata.getId().getLeft());

        data.remove(prietenieSelectata);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void setGlobalService(GlobalService globalService) {
        this.globalService = globalService;
    }
}