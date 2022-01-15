package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.exceptions.ValidationException;
import com.example.socialnetwork.service.GlobalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

public class EventsController {

    private final ObservableList<Event> dataEvents = FXCollections.observableArrayList();
    private final ObservableList<Event> dataEventsJoined = FXCollections.observableArrayList();
    private final ObservableList<Event> dataNotifications = FXCollections.observableArrayList();
    public Button btnCreateEvent;
    public Button btnJoinEvent;
    public Button btnLeaveEvent;
    private String username = Constants.username;
    private String password = Constants.password;
    private String url = Constants.url;
    private User user;
    private GlobalService globalService;

    @FXML
    private TableView<Event> tableEvents;
    @FXML
    private TableColumn<Event, String> titleCol;
    @FXML
    private TableColumn<Event, String> descriptionCol;
    @FXML
    private TableColumn<Event, LocalDateTime> dateEventCol;

    @FXML
    private TableView<Event> tableEventsJoined;
    @FXML
    private TableColumn<Event, String> titleJoinedCol;
    @FXML
    private TableColumn<Event, String> descriptionJoinedCol;
    @FXML
    private TableColumn<Event, LocalDateTime> dateEventJoinedCol;

    @FXML
    private TableView<Event> tableNotifications;
    @FXML
    private TableColumn<Event, String> notifEventTitleCol;
    @FXML
    private TableColumn<Event, String> notifTimeRemainingCol;

    @FXML
    private TextField txtEventTitle;
    @FXML
    private TextArea txtEventDescription;
    @FXML
    private DatePicker dateEventStart;

    public void setData() {
        dataEvents.addAll((Collection<? extends Event>) globalService.getEventService().getAll());

        List<Event> joinedEventsList = globalService.getParticipationService().getAllUserParticipations(user, false)
                .stream()
                .map(participation -> globalService.getEventService().findOne(participation.getId().getRight()))
                .toList();
        dataEventsJoined.addAll(joinedEventsList);

        List<Event> notifiableEvents = globalService.getParticipationService().getAllUserParticipations(user, true)
                .stream()
                .map(participation -> globalService.getEventService().findOne(participation.getId().getRight()))
                .toList();
        dataNotifications.addAll(notifiableEvents);
    }

    private void initializeTableEvents() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateEventCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateEventCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
    }

    private void initializeTableEventsJoined() {
        titleJoinedCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionJoinedCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateEventJoinedCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateEventJoinedCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
    }

    private void initializeTableNotifications() {
        notifEventTitleCol.setCellValueFactory(new PropertyValueFactory("title"));
        notifTimeRemainingCol.setCellValueFactory(cellData -> {
            LocalDateTime eventDate = cellData.getValue().getDate();
            LocalDateTime currentDate = LocalDateTime.now();
            long seconds = ChronoUnit.SECONDS.between(currentDate, eventDate);
            String timeString = "";
            if (seconds < 0) {
                timeString = "Event finished";
            } else {
                long hours = seconds / 3600;
                long minutes = (seconds % 3600) / 60;
                timeString = Long.toString(hours) + " hours, " + Long.toString(minutes) +
                        " minutes, " + Long.toString(seconds % 60) + " seconds";
            }
            return new SimpleStringProperty(timeString);
        });
    }

    @FXML
    void initialize() {
        initializeTableEvents();
        initializeTableEventsJoined();
        initializeTableNotifications();

        tableEvents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableEventsJoined.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setAll(GlobalService globalService, User user) {
        this.globalService = globalService;
        this.user = user;
        setData();
        tableEvents.setItems(dataEvents);
        tableEventsJoined.setItems(dataEventsJoined);
        tableNotifications.setItems(dataNotifications);
    }

    private void alertMessage(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.show();
    }

    public void onBtnCreateEventClicked() {
        String title = txtEventTitle.getText();
        String description = txtEventDescription.getText();
        LocalDateTime date = dateEventStart.getValue().atTime(LocalTime.MIDNIGHT);

        Event event = new Event(title, description, date, null);
        try {
            globalService.getEventService().saveEvent(event);
            dataEvents.add(event);
            alertMessage(Alert.AlertType.INFORMATION, "Success");
        } catch (ValidationException ve) {
            alertMessage(Alert.AlertType.ERROR, ve.getMessage());
        }
    }

    public void onBtnJoinEventClicked() {
        tableEvents.getSelectionModel().getSelectedItems().forEach(event -> {
            if (dataEventsJoined.stream().noneMatch(p -> event.getId().equals(p.getId()))) {
                dataEventsJoined.add(event);
                Participation participation = new Participation(user.getId(), event.getId(), false);
                globalService.getParticipationService().saveParticipation(participation);
            }
        });
    }

    public void onBtnLeaveEventAction() {
        ObservableList<Event> selectedItems = tableEventsJoined.getSelectionModel().getSelectedItems();
        dataNotifications.removeAll(selectedItems
                .stream()
                .filter(e -> {
                    Tuple<Long, Long> participationId = new Tuple<>(user.getId(), e.getId());
                    Participation participation = globalService.getParticipationService()
                            .findOne(participationId);
                    if (participation.isNotifiable()) {
                        return true;
                    }
                    return false;
                })
                .toList());

        selectedItems.forEach(event -> {
            Tuple<Long, Long> participationId = new Tuple<>(user.getId(), event.getId());
            globalService.getParticipationService().deleteParticipation(participationId);
        });

        dataEventsJoined.removeAll(selectedItems);
    }

    public void onRefreshIconPress() {
        dataNotifications.clear();
        List<Event> notifiableEvents = globalService.getParticipationService().getAllUserParticipations(user, true)
                .stream()
                .map(participation -> globalService.getEventService().findOne(participation.getId().getRight()))
                .toList();
        dataNotifications.addAll(notifiableEvents);
    }

    public void onSubscribeButtonAction() {
        tableEventsJoined.getSelectionModel().getSelectedItems().forEach(event -> {
            if (dataNotifications.stream().noneMatch(e -> e.getId().equals(event.getId()))) {
                Long userId = user.getId();
                Long eventId = event.getId();
                Participation participation = new Participation(userId, eventId, true);
                globalService.getParticipationService().updateParticipation(participation);
                dataNotifications.add(event);
            }
        });
    }

    public void OnUnsubscribeButtonAction() {
        tableNotifications.getSelectionModel().getSelectedItems().forEach(event -> {
            Long userId = user.getId();
            Long eventId = event.getId();
            Participation participation = new Participation(userId, eventId, false);
            globalService.getParticipationService().updateParticipation(participation);
        });
        dataNotifications.removeAll(tableNotifications.getSelectionModel().getSelectedItems());
    }
}