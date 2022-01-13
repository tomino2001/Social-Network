package com.example.socialnetwork.repository.db;

import com.example.socialnetwork.domain.Notification;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class NotificationDbRepository implements Repository<Long, Notification> {

    private final String username;
    private final String password;
    private final String url;

    public NotificationDbRepository(String url, String username, String password) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Override
    public Notification findOne(Long notificationId) {
        if (notificationId == null)
            throw new IllegalArgumentException("Id must be not null !");

        String sql = "SELECT * FROM notifications WHERE id = (?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, notificationId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long receiver = resultSet.getLong("receiver");
                String message = resultSet.getString("message");
                String date = resultSet.getString("date");

                Notification notification = new Notification(receiver, message, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME));
                notification.setId(id);
                return notification;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Notification> findAll() {
        String sql = "SELECT * from notifications";
        Set<Notification> notifications = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long receiver = resultSet.getLong("receiver");
                String message = resultSet.getString("message");
                String date = resultSet.getString("date");

                Notification notification = new Notification(receiver, message, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME));
                notification.setId(id);
                notifications.add(notification);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return notifications;
    }

    @Override
    public Notification save(Notification notification) {
        String sql = "INSERT INTO notifications (id_receiver, message, date) VALUES (?,?,?) RETURNING id";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, notification.getReceiver());
            preparedStatement.setString(2, notification.getMessage());
            preparedStatement.setString(3, notification.getDate().toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                notification.setId(id);
                return notification;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Long notificationId) {
        String sql = "DELETE FROM notifications WHERE id = (?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, notificationId);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Notification notification) {
        String sql = "UPDATE notifications SET id_receiver = (?), message=(?), date=(?) WHERE id = (?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, notification.getReceiver());
            preparedStatement.setString(2, notification.getMessage());
            preparedStatement.setString(3, notification.getDate().toString());
            preparedStatement.setLong(4, notification.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void remove(Notification entity) {

    }
}
