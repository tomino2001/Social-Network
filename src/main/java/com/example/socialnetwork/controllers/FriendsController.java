package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.exceptions.ValidationException;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsController {
    private final ObservableList<Friendship> data = FXCollections.observableArrayList();
    public Button btnShowAllFrdReq;
    public Button btnAddFriend;
    public Button btnExportActivityToPdf;
    private GlobalService globalService;
    private User user;
    @FXML
    private TableView<Friendship> tableView;
    @FXML
    private TableColumn<Friendship, String> columnFirstName;
    @FXML
    private TableColumn<Friendship, String> columnLastName;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextArea txtMessage;
    @FXML
    private DatePicker dateStart;
    @FXML
    private DatePicker dateEnd;

    public FriendsController() {
    }

    @FXML
    private void initialize() {
        columnFirstName.setCellValueFactory(cellData -> extractFriendLastName(cellData, true));
        columnLastName.setCellValueFactory(cellData -> extractFriendLastName(cellData, false));
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public SimpleStringProperty extractFriendLastName(TableColumn.CellDataFeatures<Friendship, String> cellData, boolean extractFirst) {
        if (!Objects.equals(cellData.getValue().getStatus(), "approved"))
            return new SimpleStringProperty("-----");
        User friend;
        Long left = cellData.getValue().getId().getLeft();
        Long right = cellData.getValue().getId().getRight();
        if (left.equals(user.getId()))
            friend = globalService.getUserService().findOne(right);
        else
            friend = globalService.getUserService().findOne(left);
        if (extractFirst)
            return new SimpleStringProperty(friend.getFirstName());
        else
            return new SimpleStringProperty(friend.getLastName());
    }

    public void setAll(GlobalService globalService, User user) {
        this.globalService = globalService;
        this.user = user;
        data.addAll(this.globalService.userFriendsList(user.getFirstName(), user.getLastName()));
        tableView.setItems(data);
    }

    @FXML
    public void onFriendReqButtonClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/friendsManagementView.fxml"));
        Parent parent = loader.load();

        data.clear();
        data.addAll(this.globalService.userFriendsList(user.getFirstName(), user.getLastName()));
        tableView.setItems(data);

        FriendsManagementController friendsManagementController = loader.getController();
        friendsManagementController.setAll(globalService, user, data);

        Scene scene = new Scene(parent);

        Stage stage = new Stage();
        stage.setTitle(user.getFirstName() + ' ' + user.getLastName() + " - Friendship requestes");
        stage.setScene(scene);
        stage.show();
    }

    public void onBtnAddFriendClicked() {
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        User friend = globalService.getUserService().findByName(firstName, lastName);
        if (friend == null) {
            alertMessage(Alert.AlertType.WARNING, "User does not exist");
            return;
        }
        Friendship friendship = new Friendship(user.getId(), friend.getId(), LocalDateTime.now());
        try {
            friendship.setStatus("pending");
            if (globalService.getFriendshipService().addFriendship(friendship) == null)
                data.add(friendship);
        } catch (ValidationException ve) {
            alertMessage(Alert.AlertType.ERROR, ve.getMessage());
        }
        alertMessage(Alert.AlertType.CONFIRMATION, "Success!");
    }

    private void alertMessage(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.show();
    }

    public void onSendIconPress() {
        List<Friendship> friendshipList = tableView.getSelectionModel().getSelectedItems();
        List<User> userList = new ArrayList<>();
        String message = txtMessage.getText();

        for (Friendship friendship : friendshipList) {
            if (friendship.getId().getLeft().equals(user.getId()))
                userList.add(globalService.getUserService().findOne(friendship.getId().getRight()));
            else
                userList.add(globalService.getUserService().findOne(friendship.getId().getLeft()));
        }
        try {
            Message message1 = new Message(user, userList, message, LocalDateTime.now());
            globalService.getMessageService().saveMessage(message1);
            alertMessage(Alert.AlertType.CONFIRMATION, "Success!");
        } catch (ValidationException ve) {
            alertMessage(Alert.AlertType.ERROR, ve.getMessage());
        }

    }

    public void onBtnExportActivityClicked() {
        LocalDate startDate = dateStart.getValue();
        LocalDate endDate = dateEnd.getValue();

        List<Friendship> friendshipList = globalService.getFriendshipService().friendshipsListDuringPeriod(user, startDate, endDate);

        data.clear();
        data.addAll(friendshipList);
        tableView.setItems(data);

        globalService.exportPdfUserActivityDuringPeriod(user, startDate, endDate);
        alertMessage(Alert.AlertType.INFORMATION, "Exported in pdfData file.");
    }

    public void onRefreshIconPress() {
        data.clear();
        data.addAll(this.globalService.userFriendsList(user.getFirstName(), user.getLastName()));
    }

    public void onDeleteImgPress(MouseEvent mouseEvent) {
        Friendship selectedFriendship = tableView.getSelectionModel().getSelectedItem();
        if (selectedFriendship == null) {
            alertMessage(Alert.AlertType.ERROR, "First select a user");
            return;
        }
        globalService.getFriendshipService().removeFriendship(
                selectedFriendship.getId().getLeft(), selectedFriendship.getId().getRight());
        globalService.getFriendshipService().removeFriendship(
                selectedFriendship.getId().getRight(), selectedFriendship.getId().getLeft());

        data.remove(selectedFriendship);
        alertMessage(Alert.AlertType.CONFIRMATION, "Success!");
    }
}