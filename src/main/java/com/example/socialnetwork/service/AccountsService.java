package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Account;
import com.example.socialnetwork.repository.Repository;

import java.util.Objects;

public class AccountsService {
    private final Repository<Long, Account> repo;

    public AccountsService(Repository<Long, Account> repo) {
        this.repo = repo;
    }

    public void addUAccount(Account account) {
        repo.save(account);
    }

    public void deleteAccount(Long id) {
        repo.delete(id);
    }

    public Account findOne(Long id) {
        return repo.findOne(id);
    }

    public Iterable<Account> getAll(){
        return repo.findAll();
    }

    public Account getAccountByUsernameAndPassword(String username, String password){
        Iterable<Account> accounts = repo.findAll();
        Account accountRez = null;
        for (Account account: accounts) {
            if(Objects.equals(account.getUsername(), username) && Objects.equals(account.getPassword(), password)){
                accountRez = account;
                break;
            }
        }
        return accountRez;
    }
}
