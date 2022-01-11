package com.example.socialnetwork.repository.db;

import com.example.socialnetwork.domain.Event;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDbRepository implements Repository<Long, Event> {

    private final String username;
    private final String password;
    private final String url;

    public EventDbRepository(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Override
    public Event findOne(Long eventId) {
        if (eventId == null)
            throw new IllegalArgumentException("Id must be not null !");

        String sql = "SELECT * FROM events WHERE id = (?)";
        String sql1 = "Select * FROM user_event WHERE id_event = (?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, eventId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String date = resultSet.getString("sent_date");

                List<Long> userList = new ArrayList<>();
                try (Connection connection1 = DriverManager.getConnection(url, username, password);
                PreparedStatement preparedStatement1 = connection1.prepareStatement(sql)){
                preparedStatement1.setLong(1, eventId);
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                    var userId = resultSet1.getLong("id_user");
                    userList.add(userId);
                }catch (SQLException exception) {
                    exception.printStackTrace();
                }

                Event event = new Event(title, description, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME), userList);
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
        String sql1 = "SELECT * from user_event where id_event = (?)";
        Set<Event> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String date = resultSet.getString("date");

                List<Long> userList = new ArrayList<>();
                try (Connection connection1 = DriverManager.getConnection(url, username, password);
                     PreparedStatement preparedStatement1 = connection1.prepareStatement(sql)){
                    preparedStatement1.setLong(1, id);
                    ResultSet resultSet1 = preparedStatement1.executeQuery();
                    var userId = resultSet1.getLong("id_user");
                    userList.add(userId);
                }catch (SQLException exception) {
                    exception.printStackTrace();
                }

                Event event = new Event(title, description, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME), userList);
                event.setId(id);
                events.add(event);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        String sql = "INSERT INTO events (title, description, date) VALUES (?,?,?) RETURNING id";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setString(3, entity.getDate().toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                entity.setId(id);
                return entity;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Long eventId) {
        String sql = "DELETE FROM messages WHERE id = (?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, eventId);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Event entity) {
        String sql = "INSERT INTO user_event (id_user, id_event) VALUES (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, entity.getId());
            for (Long idUser: entity.getUsers()) {
                preparedStatement.setLong(2, idUser);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void remove(Event entity) {

    }
}
