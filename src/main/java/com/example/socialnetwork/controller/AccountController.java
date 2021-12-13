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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountController {

    @FXML
    public Button btnShowAllFrdReq;

    private Utilizator utilizator;

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    @FXML
    public void onFriendReqButtonClicked() throws IOException {
        Stage stage = (Stage) btnShowAllFrdReq.getScene().getWindow();
        stage.close();
        openPrieteniiTable(utilizator);
    }

    private void openPrieteniiTable(Utilizator utilizator) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/prieteniiView.fxml"));
        Parent parent = loader.load();
        PrieteniiController prieteniiController = loader.getController();
        prieteniiController.setUtilizator(utilizator);

        Scene scene = new Scene(parent, 750, 750);
        Stage stage = new Stage();
        stage.setTitle(utilizator.getFirstName() + ' ' + utilizator.getLastName() + " - Friendship requestes");
        stage.setScene(scene);
        stage.show();
    }

}