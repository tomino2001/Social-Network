package socialnetwork.repository.db;

import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MessagesDbRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Utilizator> validator;

    public MessagesDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message save(Message entity) {
        String sql = "insert into messages (id_sursa, id_dest, mesaj, data_expediere) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);) {
            Long idSursa = entity.getFrom().getId();
            entity.getTo().forEach(utilizator -> {
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setLong(1, idSursa);
                    statement.setLong(2, utilizator.getId());
                    statement.setString(3, entity.getMessage());
                    statement.setString(4, String.valueOf(entity.getDate()));
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entity;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void update(Message entity) {
        String sql = "update messages set replied=? where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setLong(2, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message findOne(Long id) {
        String sql = "select * from messages where id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
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
            Utilizator utilizator1 = new Utilizator(firstName, lastName);
            utilizator1.setId(id1);

            userStatement = connection.prepareStatement(userSql);
            userStatement.setLong(1, idSursa);
            userResultSet = userStatement.executeQuery();
            userResultSet.next();

            id1 = userResultSet.getLong("id");
            firstName = userResultSet.getString("first_name");
            lastName = userResultSet.getString("last_name");
            Utilizator utilizator2 = new Utilizator(firstName, lastName);
            utilizator2.setId(id1);

            Message message = new Message(utilizator2, Arrays.asList(utilizator1), text, LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME));
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
        String sql = "select * from messages";

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
                Utilizator utilizator1 = new Utilizator(firstName, lastName);
                utilizator1.setId(id1);

                userStatement = connection.prepareStatement(userSql);
                userStatement.setLong(1, idSursa);
                userResultSet = userStatement.executeQuery();
                userResultSet.next();

                id1 = userResultSet.getLong("id");
                firstName = userResultSet.getString("first_name");
                lastName = userResultSet.getString("last_name");
                Utilizator utilizator2 = new Utilizator(firstName, lastName);
                utilizator2.setId(id1);

                Message message = new Message(utilizator2, Arrays.asList(utilizator1), text, LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME));
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