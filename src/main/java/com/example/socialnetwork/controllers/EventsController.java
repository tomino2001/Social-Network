package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.Event;
import com.example.socialnetwork.domain.Notification;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDateTime;
import java.util.Collection;

public class EventsController {

    public Button btnCreateEvent;
    public Button btnJoinEvent;
    public Button btnDeleteEvent;
    private User user;
    private GlobalService globalService;

    @FXML
    private TableView<Event> tableEvent;
    @FXML
    private TableColumn<Event, String> titleCol;
    @FXML
    private TableColumn<Event, String> descriptionCol;
    @FXML
    private TableColumn<Event, LocalDateTime>  dateEventCol;

    @FXML
    private TableView<Notification> tableNotification;
    @FXML
    private TableColumn<Notification, String>  messageCol;
    @FXML
    private TableColumn<Notification, String> dateNotificationCol;

    private final ObservableList<Event> dataEvent = FXCollections.observableArrayList();
    private final ObservableList<Notification> dataNotification = FXCollections.observableArrayList();

    public void setService(GlobalService globalService) {
        this.globalService = globalService;
    }

    public void setUtilizator(User user) {
        this.user = user;
    }

    public void  setData(){
        dataEvent.addAll((Collection<? extends Event>) globalService.getEventService().getAll());
        dataNotification.addAll((Collection<? extends Notification>) globalService.getNotificationService().get_all());
    }

    @FXML
    void initialize() {
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        dateEventCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateEventCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));


        messageCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));
        dateNotificationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));

        tableEvent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableNotification.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    public void setAll(GlobalService globalService, User user) {
        setService(globalService);
        setUtilizator(user);
        setData();
        tableEvent.setItems(dataEvent);
        tableNotification.setItems(dataNotification);
    }

    private void alertMessage(Alert.AlertType tipAlerta, String mesaj) {
        Alert alert = new Alert(tipAlerta, mesaj);
        alert.show();
    }

    public void onBtnCreateEvenetClicked(ActionEvent actionEvent) {
    }

    public void onBtnJoinEventClicked(ActionEvent actionEvent) {
    }

    public void onBtnDeleteEventClicked(ActionEvent actionEvent) {
    }
}
