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
import com.example.socialnetwork.service.UtilizatoriPrieteniiService;
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

    private Validator<Prietenie> validatorPrietenie = new PrietenieValidator();
    private Validator<Utilizator> validatorUtilizator = new UtilizatorValidator();

    private Repository<Long, Utilizator> utilizatorDbRepository =
            new UtilizatorDbRepository(Constants.url, Constants.username, Constants.password, validatorUtilizator);
    private Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository =
            new PrieteniiDbRepository(Constants.url, Constants.username, Constants.password, validatorPrietenie);
    private Repository<Long, Message> mesajeRepository =
            new MessagesDbRepository(Constants.url, Constants.username, Constants.password, null);

    private UtilizatorService utilizatorService = new UtilizatorService(utilizatorDbRepository);
    private PrietenieService prietenieService = new PrietenieService(prietenieDbRepository);
    private MesajeService mesajeService = new MesajeService(mesajeRepository);
    private UtilizatoriPrieteniiService utilizatoriPrieteniiService = new
            UtilizatoriPrieteniiService(utilizatorService, prietenieService, mesajeService);


    public AccountController() {
        prietenieDbRepository = new PrieteniiDbRepository(Constants.url, Constants.username, Constants.password, validatorPrietenie);
    }


    private final ObservableList<Utilizator> data = FXCollections.observableArrayList();
    @FXML
    private Button btnAddFriend;

    @FXML
    private Button btnRemoveFriend;

    @FXML
    private Button btnShowAllFrdReq;

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
//        tableView = new TableView<>();
//        TableColumn<Utilizator,String> columnName=new TableColumn<>("FirstName");
//        TableColumn<Utilizator,String> columnLastName=new TableColumn<>("LastName");
//        tableView.getColumns().addAll(columnName,columnLastName);

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
        data.setAll(utilizatoriPrieteniiService.utilizatoriPrieteniCuUtilizator(utilizator.getFirstName(), utilizator.getLastName()));
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
        Utilizator prieten = utilizatorService.findByName(firstName, lastName);
        if (prieten == null) {
            alertMessage(Alert.AlertType.WARNING, "Utilizatorul nu exista");
            return;
        }
        Prietenie prietenie = new Prietenie(utilizator.getId(), prieten.getId(), LocalDateTime.now());
        prietenie.setStatus("pending");
        prietenieService.addPrietenie(prietenie);
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
        prietenieService.removePrietenie(utilizatorSelectat.getId(), utilizator.getId());
        prietenieService.removePrietenie(utilizator.getId(), utilizatorSelectat.getId());
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
        updateTable();
    }
}