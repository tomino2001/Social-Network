package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.domain.validators.PrietenieValidator;
import com.example.socialnetwork.domain.validators.UtilizatorValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.MessagesDbRepository;
import com.example.socialnetwork.repository.db.PrieteniiDbRepository;
import com.example.socialnetwork.repository.db.UtilizatorDbRepository;
import com.example.socialnetwork.service.MesajeService;
import com.example.socialnetwork.service.PrietenieService;
import com.example.socialnetwork.service.UtilizatorService;
import com.example.socialnetwork.service.GlobalService;
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

public class AccountController {
    private GlobalService globalService;

    public AccountController() {
    }


    private final ObservableList<Utilizator> data = FXCollections.observableArrayList();

    @FXML
    private TableView<Utilizator> tableView;

    @FXML
    private TableColumn<Utilizator, String> columnFirstName;

    @FXML
    private TableColumn<Utilizator, String> columnLastName;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private void initialize() {

    }

    private Utilizator utilizator;

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
        loadTable();
    }


    private void loadTable() {
        columnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        updateTable();
    }

    private void updateTable() {
        data.setAll(globalService.utilizatoriPrieteniCuUtilizator(utilizator.getFirstName(), utilizator.getLastName()));
        tableView.setItems(data);
    }

    @FXML
    public void onFriendReqButtonClicked() throws IOException {
        openPrieteniiTable(utilizator);
    }

    private void openPrieteniiTable(Utilizator utilizator) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/prieteniiView.fxml"));
        Parent parent = loader.load();
        PrieteniiController prieteniiController = loader.getController();
        prieteniiController.setService(globalService);
        prieteniiController.setUtilizator(utilizator);

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
        globalService.getPrietenieService().addPrietenie(prietenie);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    private void alertMessage(Alert.AlertType tipAlerta, String mesaj) {
        Alert alert = new Alert(tipAlerta, mesaj);
        alert.show();
    }

    public void onBtnRemoveFriendClicked() {
        Utilizator utilizatorSelectat = tableView.getSelectionModel().getSelectedItem();
        if (utilizatorSelectat == null){
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat un utilizator!");
            return;
        }
        globalService.getPrietenieService().removePrietenie(utilizatorSelectat.getId(), utilizator.getId());
        globalService.getPrietenieService().removePrietenie(utilizator.getId(), utilizatorSelectat.getId());
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
        updateTable();
    }

    public void setGlobalService(GlobalService globalService) {
        this.globalService = globalService;
    }
}