package com.example.socialnetwork.repository.db;

import com.example.socialnetwork.domain.Participation;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.repository.paging.Page;
import com.example.socialnetwork.repository.paging.Pageable;
import com.example.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ParticipationDbRepository implements PagingRepository<Tuple<Long, Long>, Participation> {

    private final String username;
    private final String password;
    private final String url;

    public ParticipationDbRepository(String url, String username, String password) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Override
    public Participation findOne(Tuple<Long, Long> longLongTuple) {
        String sqlQuery = "select * from participations where id_user = ? and id_event = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery);) {

            statement.setLong(1, longLongTuple.getLeft());
            statement.setLong(2, longLongTuple.getRight());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id_user = resultSet.getLong("id_user");
                Long id_event = resultSet.getLong("id_event");
                boolean notifiable = resultSet.getBoolean("notifiable");

                Participation participation = new Participation(id_user, id_event, notifiable);
                return participation;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Participation> findAll() {
        String sqlQuery = "select * from participations";
        Set<Participation> participations = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id_user = resultSet.getLong("id_user");
                Long id_event = resultSet.getLong("id_event");
                boolean notifiable = resultSet.getBoolean("notifiable");

                Participation participation = new Participation(id_user, id_event, notifiable);
                participations.add(participation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;
    }

    @Override
    public Participation save(Participation entity) {
        Tuple<Long, Long> participationId = entity.getId();
        if (findOne(participationId) != null)
            return entity;

        String sqlQuery = "insert into participations(id_user, id_event) values (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery);) {
            statement.setLong(1, entity.getId().getLeft());
            statement.setLong(2, entity.getId().getRight());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Tuple<Long, Long> participationId) {
        String sqlQuery = "delete from participations where id_user = ? and id_event = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery);) {
            statement.setLong(1, participationId.getLeft());
            statement.setLong(2, participationId.getRight());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Participation entity) {
        String sqlQuery = "update participations set notifiable = ? where id_user = ? and id_event = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlQuery);) {
            statement.setBoolean(1, entity.isNotifiable());
            statement.setLong(2, entity.getId().getLeft());
            statement.setLong(3, entity.getId().getRight());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Participation entity) {

    }

    @Override
    public Page<Participation> findAll(Pageable pageable) {
        return null;
    }
}
