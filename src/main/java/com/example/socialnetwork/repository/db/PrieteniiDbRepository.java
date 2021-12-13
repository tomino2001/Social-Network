package com.example.socialnetwork.repository.db;


import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class PrieteniiDbRepository implements Repository<Tuple<Long, Long>, Prietenie> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Prietenie> validator;

    public PrieteniiDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Prietenie findOne(Tuple<Long, Long> id) {
        String sql = "select * from friendships where id_friend1=? and id_friend2=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                Long id1 = resultSet.getLong("id_friend1");
                Long id2 = resultSet.getLong("id_friend2");
                String date = resultSet.getString("creation_date");
                String status = resultSet.getString("status");
                Prietenie prietenie = new Prietenie(id1, id2, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME));
                prietenie.setStatus(status);
                return prietenie;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenii = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id_friend1");
                Long id2 = resultSet.getLong("id_friend2");
                String date = resultSet.getString("creation_date");
                String status = resultSet.getString("status");
                Prietenie prietenie = new Prietenie(id1, id2, LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME));
                prietenie.setStatus(status);
                prietenii.add(prietenie);
            }
            return prietenii;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenii;
    }

    @Override
    public Prietenie save(Prietenie entity) {
        Tuple<Long, Long> id1 = entity.getId();
        Tuple<Long, Long> id2 = new Tuple<>(entity.getId().getRight(), entity.getId().getLeft());

        if (findOne(id1) != null || findOne(id2) != null)
            return entity;
        String sql = "insert into friendships (id_friend1, id_friend2, creation_date, status) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            String dateTime = entity.getDate().format(DateTimeFormatter.ISO_DATE_TIME);
            ps.setString(3, dateTime);
            ps.setString(4, entity.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Tuple<Long, Long> id) {
        String sql = "delete from friendships where id_friend1=? and id_friend2=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id.getLeft());
            ps.setLong(2, id.getRight());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Prietenie entity) {
        String sql = "update friendships set id_friend1=?, id_friend2=?, creation_date=?, status = ? " +
                "where id_friend1=? and id_friend2=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            String dateTime = entity.getDate().format(DateTimeFormatter.ISO_DATE_TIME);
            ps.setString(3, dateTime);
            ps.setString(4, entity.getStatus());
            ps.setLong(5, entity.getId().getLeft());
            ps.setLong(6, entity.getId().getRight());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}