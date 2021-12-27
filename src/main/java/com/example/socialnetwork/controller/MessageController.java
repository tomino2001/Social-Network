package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Message;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDateTime;


public class MessageController {

    private Utilizator utilizator;
    private GlobalService globalService;

    public void setService(GlobalService globalService) {
        this.globalService = globalService;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    private void setData() {
        data.addAll(globalService.getMesajeService().find_all_msg_recived_by_user(utilizator));
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

    public void setAll(GlobalService globalService, Utilizator utilizator) {
        setService(globalService);
        setUtilizator(utilizator);
        setData();
        table.setItems(data);
    }


}
