package com.example.socialnetwork.controller;

import com.example.socialnetwork.MainApp;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AccountController {
    public Button btnShowAllFrdReq;
    public Button btnAddFriend;
    public Button btnRemoveFriend;
    public Button btnLogout;
    public Button btnSendMessage;
    public Button btnShowAllMessages;


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
    private TextArea txtMessage;

    @FXML
    private void initialize() {
        columnFirstName.setCellValueFactory(cellData -> extractFriendLastName(cellData, true));
        columnLastName.setCellValueFactory(cellData -> extractFriendLastName(cellData, false));
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public SimpleStringProperty extractFriendLastName(TableColumn.CellDataFeatures<Prietenie, String> cellData, boolean extractFirst) {
        if (!Objects.equals(cellData.getValue().getStatus(), "approved"))
            return new SimpleStringProperty("-----");
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

    public void initAccount(Utilizator utilizator, GlobalService globalService) {
        setGlobalService(globalService);
        this.utilizator = utilizator;
        data.addAll(this.globalService.listaPrieteniUtilizator(utilizator.getFirstName(), utilizator.getLastName()));
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

    public void onBtnLogoutAction() throws IOException {
        Stage accountStage = (Stage) btnLogout.getScene().getWindow();
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

    public void onBtnSendMessageClicked() {
        List<Prietenie> prietenieList = tableView.getSelectionModel().getSelectedItems();
        List<Utilizator> utilizatorList = new ArrayList<>();
        String message = txtMessage.getText();

        for (Prietenie prietenie: prietenieList) {
            if(prietenie.getId().getLeft().equals(utilizator.getId()))
                utilizatorList.add(globalService.getUtilizatorService().findOne(prietenie.getId().getRight()));
            else
                utilizatorList.add(globalService.getUtilizatorService().findOne(prietenie.getId().getLeft()));
        }
        Message message1 = new Message(utilizator, utilizatorList, message, LocalDateTime.now());
        globalService.getMesajeService().saveMessage(message1);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void btnShowAllMessagesClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/messageView.fxml"));
        Parent parent = loader.load();
        MessageController messageController = loader.getController();
        messageController.setAll(globalService, utilizator);

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(utilizator.getFirstName() + ' ' + utilizator.getLastName() + " - All recived messages");
        stage.setScene(scene);
        stage.show();
    }
}