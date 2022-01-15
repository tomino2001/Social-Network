package com.example.socialnetwork.repository.db;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.paging.Page;
import com.example.socialnetwork.repository.paging.Pageable;
import com.example.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class AccountDbRepository implements PagingRepository<Long, Account> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Account> validator;

    public AccountDbRepository(String url, String username, String password, Validator<Account> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Account findOne(Long id) {
        String sql = "select * from accounts where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id1 = resultSet.getLong("id");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            Account account = new Account(username, password);
            account.setId(id1);
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Account> findAll() {
        Set<Account> accounts = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from accounts");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                Account account = new Account(username, password);
                account.setId(id);
                accounts.add(account);
            }
            return accounts;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public Account save(Account account) {
        validator.validate(account);
        String sql = "insert into accounts (username, password) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from accounts where id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Account account) {
    }

    @Override
    public void remove(Account account) {

    }

    @Override
    public Page<Account> findAll(Pageable pageable) {
        return null;
    }
}