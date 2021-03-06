package com.example.socialnetwork.repository.db;

import com.example.socialnetwork.domain.Event;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.paging.Page;
import com.example.socialnetwork.repository.paging.Pageable;
import com.example.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class EventDbRepository implements PagingRepository<Long, Event> {

    private final String username;
    private final String password;
    private final String url;
    private Validator<Event> validator;

    public EventDbRepository(String url, String username, String password, Validator<Event> eventValidator) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.validator = eventValidator;
    }

    @Override
    public Event findOne(Long eventId) {
        if (eventId == null)
            throw new IllegalArgumentException("Id must be not null !");

        String sql = "SELECT * FROM events WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, eventId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String date = resultSet.getString("date");

                Event event = new Event(title, description, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME), null);
                event.setId(id);
                return event;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        String sql = "SELECT * from events";
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String date = resultSet.getString("date");

                Event event = new Event(title, description, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME), null);
                event.setId(id);
                events.add(event);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event event) {
        validator.validate(event);
        String sql = "INSERT INTO events (title, description, date) VALUES (?,?,?) RETURNING id";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, event.getTitle());
            preparedStatement.setString(2, event.getDescription());
            preparedStatement.setString(3, event.getDate().toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                event.setId(id);
                return event;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Long eventId) {
        String sql = "DELETE FROM messages WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, eventId);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Event event) {
        validator.validate(event);
        String sql = "INSERT INTO user_event (id_user, id_event) VALUES (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, event.getId());
            for (Long idUser : event.getUsers()) {
                preparedStatement.setLong(2, idUser);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void remove(Event event) {

    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return null;
    }
}