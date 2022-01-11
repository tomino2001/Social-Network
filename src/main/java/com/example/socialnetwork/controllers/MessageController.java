package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MessageController {

    public Button btnReply;
    public Button btnDeleteMessage;
    public Button btnShowMessage;
    public Button btnExportPdf;
    private User user;
    private GlobalService globalService;

    public void setService(GlobalService globalService) {
        this.globalService = globalService;
    }

    public void setUtilizator(User user) {
        this.user = user;
    }

    private void setData() {
        data.addAll(globalService.getMesajeService().find_all_msg_recived_by_user(user));
    }

    @FXML
    private TableView<Message> table;
    @FXML
    private TableColumn<Message, String> firstNameCol;
    @FXML
    private TableColumn<Message, String> lastNameCol;
    @FXML
    private TableColumn<Message, String> messageCol;
    @FXML
    private TableColumn<Message, LocalDateTime> dateCol;
    @FXML
    private TextArea txtMessage;
    @FXML
    private DatePicker dateStart;
    @FXML
    private DatePicker dateEnd;
    @FXML
    private TextField firstNameTxt;
    @FXML
    private TextField lastNameTxt;

    private final ObservableList<Message> data = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        firstNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFrom().getFirstName()));
        lastNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFrom().getLastName()));
        messageCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setAll(GlobalService globalService, User user) {
        setService(globalService);
        setUtilizator(user);
        setData();
        table.setItems(data);
    }

    private void alertMessage(Alert.AlertType tipAlerta, String mesaj) {
        Alert alert = new Alert(tipAlerta, mesaj);
        alert.show();
    }

    public void onBtnReplyClicked() {
        List<Message> messageList = table.getSelectionModel().getSelectedItems();
        List<User> userList = new ArrayList<>();
        String messageToSend = txtMessage.getText();

        for (Message message: messageList) {
            userList.add(message.getFrom());
            globalService.getMesajeService().updateMessage(message);
        }
        Message message = new Message(user, userList, messageToSend, LocalDateTime.now());
        globalService.getMesajeService().saveMessage(message);
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void onBtnDeleteMessageClicked() {
        List<Message> messageList = table.getSelectionModel().getSelectedItems();
        if (messageList == null) {
            alertMessage(Alert.AlertType.ERROR, "Mai intai trebuie selectat un mesaj!");
            return;
        }
        for (Message message: messageList) {
            globalService.getMesajeService().removeMessage(message);
            this.data.remove(message);
        }
        alertMessage(Alert.AlertType.CONFIRMATION, "Succes!");
    }

    public void onBtnShowMessageClicked() {
        LocalDate startDate = dateStart.getValue();
        LocalDate endDate = dateEnd.getValue();

        String firstName = firstNameTxt.getText();
        String lastName = lastNameTxt.getText();

        User user1 = globalService.getUtilizatorService().findByName(firstName, lastName);
        if(user1 == null) alertMessage(Alert.AlertType.ERROR, "User not found");

        List<Message> messageList = globalService.getMesajeService().listaMesajePrimiteDeLaUtilizatorXInPerioadaX(
                user, user1, startDate, endDate);

        data.clear();
        data.addAll(messageList);
        table.setItems(data);

        alertMessage(Alert.AlertType.INFORMATION, "Exported in pdfData file.");
    }

    public void onBtnExportPdfClicked() {
        LocalDate startDate = dateStart.getValue();
        LocalDate endDate = dateEnd.getValue();

        String firstName = firstNameTxt.getText();
        String lastName = lastNameTxt.getText();

        User user1 = globalService.getUtilizatorService().findByName(firstName, lastName);

        List<Message> messageList = globalService.getMesajeService().listaMesajePrimiteDeLaUtilizatorXInPerioadaX(
                user, user1, startDate, endDate);

        globalService.exportToPdfListaMesajePrimiteDeLaUtilizatorXInPerioadaX(user, user1, startDate, endDate);

        data.clear();
        data.addAll(messageList);
        table.setItems(data);

        alertMessage(Alert.AlertType.INFORMATION, "Exported in pdfData file.");
    }
}
