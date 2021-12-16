package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Constants;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.domain.validators.PrietenieValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.PrieteniiDbRepository;
import com.example.socialnetwork.service.GlobalService;
import com.example.socialnetwork.service.PrietenieService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PrieteniiController {
    private GlobalService globalService;
    private Utilizator utilizator;

    public void setService(GlobalService globalService) {
        this.globalService = globalService;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
        loadTable();
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

    private final ObservableList<Prietenie> data = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        sentRecivedCol.setCellValueFactory(cellData ->{
            if(Objects.equals(cellData.getValue().getId().getLeft(), utilizator.getId()))
                return new SimpleStringProperty("Sent to");
            return new SimpleStringProperty("Recived from");
        });

        firstNameCol.setCellValueFactory(cellData ->{
            if (!Objects.equals(cellData.getValue().getId().getLeft(), utilizator.getId()))
                return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
                        cellData.getValue().getId().getLeft()).getFirstName());
            return new SimpleStringProperty(globalService.getUtilizatorService().findOne(
                    cellData.getValue().getId().getRight()).getFirstName());
        });
        lastNameCol.setCellValueFactory(cellData ->{
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

    public void loadTable() {
        List<Prietenie> prietenieList = globalService.getPrietenieService().listaCereriPrietenieUtilizatorALL(utilizator);
        data.setAll(prietenieList);
        table.setItems(data);
    }


}
