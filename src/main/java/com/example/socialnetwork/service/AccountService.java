package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.repository.Repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class AccountService {
    private final Repository<Long, Account> repo;

    public AccountService(Repository<Long, Account> repo) {
        this.repo = repo;
    }

    public void addUAccount(Account account) {
        repo.save(account);
    }

//    public void deleteAccount(Long id) {
//        repo.delete(id);
//    }

    public Account findOne(Long id) {
        return repo.findOne(id);
    }
    
    public Account getAccountByUsernameAndPassword(String username, String password) {
        Iterable<Account> accounts = repo.findAll();
        Account accountRes = null;
        for (Account account : accounts) {
            if (Objects.equals(account.getUsername(), username) && Objects.equals(account.getPassword(), password)) {
                accountRes = account;
                break;
            }
        }
        return accountRes;
    }

    public String hashPassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(passwordToHash.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // This bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
