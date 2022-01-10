package com.example.socialnetwork.repository.db;

import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MessagesDbRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public MessagesDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message save(Message entity) {
        String sql_messages = "insert into messages (mesaj, data_expediere) values (?, ?)";
        String sql_messages_info = "insert into messages_info (id_mesaj, id_sursa, id_dest, replied) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);) {
            Long idSursa = entity.getFrom().getId();
            Long messageId;
            try {
                PreparedStatement statement = connection.prepareStatement(sql_messages, Statement.RETURN_GENERATED_KEYS);

                statement.setString(1, entity.getMessage());
                statement.setString(2, String.valueOf(entity.getDate()));
                statement.executeUpdate();

                ResultSet keys = statement.getGeneratedKeys();
                keys.next();
                messageId = keys.getLong(1);

                entity.getTo().forEach(utilizator -> {
                    try {
                        PreparedStatement statement2 = connection.prepareStatement(sql_messages_info);
                        statement2.setLong(1, messageId);
                        statement2.setLong(2, idSursa);
                        statement2.setLong(3, utilizator.getId());
                        statement2.setBoolean(4, false);
                        statement2.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    @Override
    public void delete(Long id) {
    }

    public void remove(Message entity){
        String sql = "Delete from messages_info where id_mesaj = ? and id_sursa = ? and id_dest = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getFrom().getId());
            ps.setLong(3, entity.getTo().get(0).getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Message entity) {
        String sql = "update messages_info set replied=? where id_mesaj = ? and id_sursa = ? and id_dest = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setLong(2, entity.getId());
            ps.setLong(3, entity.getFrom().getId());
            ps.setLong(4, entity.getTo().get(0).getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message findOne(Long id) {
        String sql = "select * from messages where id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long idDest = resultSet.getLong("id_dest");
            Long idSursa = resultSet.getLong("id_sursa");
            String text = resultSet.getString("mesaj");
            String data = resultSet.getString("data_expediere");

            String userSql = "select * from users where id = ?";
            PreparedStatement userStatement = connection.prepareStatement(userSql);
            userStatement.setLong(1, idDest);
            ResultSet userResultSet = userStatement.executeQuery();
            userResultSet.next();

            Long id1 = userResultSet.getLong("id");
            String firstName = userResultSet.getString("first_name");
            String lastName = userResultSet.getString("last_name");
            User user1 = new User(firstName, lastName);
            user1.setId(id1);

            userStatement = connection.prepareStatement(userSql);
            userStatement.setLong(1, idSursa);
            userResultSet = userStatement.executeQuery();
            userResultSet.next();

            id1 = userResultSet.getLong("id");
            firstName = userResultSet.getString("first_name");
            lastName = userResultSet.getString("last_name");
            User user2 = new User(firstName, lastName);
            user2.setId(id1);

            Message message = new Message(user2, Arrays.asList(user1), text, LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME));
            message.setId(id);

            return message;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> mesaje = new HashSet<>();
        String sql =
                "select m.id, mi.id_sursa, mi.id_dest, m.mesaj, m.data_expediere from messages m " +
                "inner join messages_info mi " +
                "on m.id = mi.id_mesaj " +
                "order by m.data_expediere desc";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idDest = resultSet.getLong("id_dest");
                Long idSursa = resultSet.getLong("id_sursa");
                String text = resultSet.getString("mesaj");
                String data = resultSet.getString("data_expediere");

                String userSql = "select * from users where id = ?";
                PreparedStatement userStatement = connection.prepareStatement(userSql);
                userStatement.setLong(1, idDest);
                ResultSet userResultSet = userStatement.executeQuery();
                userResultSet.next();

                Long id1 = userResultSet.getLong("id");
                String firstName = userResultSet.getString("first_name");
                String lastName = userResultSet.getString("last_name");
                User user1 = new User(firstName, lastName);
                user1.setId(id1);

                userStatement = connection.prepareStatement(userSql);
                userStatement.setLong(1, idSursa);
                userResultSet = userStatement.executeQuery();
                userResultSet.next();

                id1 = userResultSet.getLong("id");
                firstName = userResultSet.getString("first_name");
                lastName = userResultSet.getString("last_name");
                User user2 = new User(firstName, lastName);
                user2.setId(id1);

                Message message = new Message(user2, Arrays.asList(user1), text, LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME));
                message.setId(id);
                mesaje.add(message);
            }
            return mesaje;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}