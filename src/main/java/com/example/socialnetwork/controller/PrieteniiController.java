package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Constants;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.domain.validators.PrietenieValidator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.PrieteniiDbRepository;
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
import java.util.ArrayList;
import java.util.List;

public class PrieteniiController {
    private Utilizator utilizator;
    Validator<Prietenie> validatorPrietenie = new PrietenieValidator();
    private Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository =
            new PrieteniiDbRepository(Constants.url, Constants.username, Constants.password, validatorPrietenie);
    private PrietenieService prietenieService = new PrietenieService(prietenieDbRepository);

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
        loadTable();
    }

    @FXML
    private TableView<Prietenie> table;
    @FXML
    private TableColumn<Prietenie, String> idCol;
    @FXML
    private TableColumn<Prietenie, String> statusCol;
    @FXML
    private TableColumn<Prietenie, LocalDateTime> dateCol;

    private final ObservableList<Prietenie> data = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        idCol.setCellValueFactory(cellData -> {
            Tuple<Long, Long> id = cellData.getValue().getId();
            return new SimpleStringProperty(id.getLeft().toString() + " -> " + id.getRight().toString());
        });
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
    }

    public void loadTable() {
        // var iterable = prietenieService.getAll();
        // List<Prietenie> result = new ArrayList<>();
        List<Prietenie> prietenieList = prietenieService.listaCereriPrietenieUtilizatorALL(utilizator);
        //  iterable.forEach(result::add);
        data.setAll(prietenieList);
        table.setItems(data);
    }
}
