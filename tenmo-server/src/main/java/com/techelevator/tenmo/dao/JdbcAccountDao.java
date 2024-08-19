package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Account getAccountByID(int userId) {
        Account account = null;
        //TODO implement SQL to get account object using account_id
        try {
        //TODO
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public Account getAccountByUserID(int userId) {
        Account account = null;
        //TODO get account object using user_id
        try {
        //TODO
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    @Override
    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        //TODO get all accounts
        try {
        //TODO
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return accounts;
    }

    @Override
    public BigDecimal getBalanceByUserId(int userId){
        BigDecimal balance = null;
        //TODO get account balance using user_id
        try {
        //TODO
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return balance;
    }

    @Override
    public Account updateAccountBalance(Account account) {
        Account updatedAccount = null;
        //TODO 
        try {
        //TODO
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedAccount;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setUserId(rs.getInt("user_id"));
        return account;
    }
}
