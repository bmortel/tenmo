package com.techelevator.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;

public class JdbcAccountDaoTest {

    private JdbcTemplate jdbcTemplate;
    private JdbcAccountDao accountDao;

    @Before
    public void setup() {
        // Setting up the JdbcTemplate and JdbcAccountDao for testing
        jdbcTemplate = mock(JdbcTemplate.class);
        accountDao = new JdbcAccountDao(jdbcTemplate);
    }


    // Method to map a row from the database to an Account object
    private Account mapRowToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getBigDecimal("balance"));
        return account;
    }

    @Test
    public void testGetAccountByID_nonExistingAccount() {
        // Testing the scenario where the account does not exist in the database
        // Mocking the behavior of querying for an account by ID
        // Verifying that null is returned

        SqlRowSet mockResults = mock(SqlRowSet.class);
        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account WHERE account_id = ?", 1))
                .thenReturn(mockResults);
        when(mockResults.next()).thenReturn(false);

        Account account = accountDao.getAccountByID(1);
        assertEquals(null, account);
    }


    @Test(expected = DaoException.class)
    public void testGetAccountByID_cannotGetConnectionException() {
        // Testing the scenario where a CannotGetJdbcConnectionException is thrown
        // Verifying that a DaoException is thrown

        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account WHERE account_id = ?", 1))
                .thenThrow(new CannotGetJdbcConnectionException("Connection error"));

        accountDao.getAccountByID(1);
    }

    @Test
    public void testGetAccountByUserID_existingAccount() {
        // Testing the scenario where an account exists for a given user ID
        // Mocking the behavior of querying for an account by user ID
        // Verifying that the correct Account object is returned

        SqlRowSet mockResults = mock(SqlRowSet.class);
        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account WHERE user_id = ?", 123))
                .thenReturn(mockResults);
        when(mockResults.next()).thenReturn(true);
        when(mockResults.getInt("account_id")).thenReturn(1);
        when(mockResults.getInt("user_id")).thenReturn(123);
        when(mockResults.getBigDecimal("balance")).thenReturn(new BigDecimal("100.00"));

        Account account = accountDao.getAccountByUserID(123);
        assertEquals(1, account.getAccountId());
        assertEquals(123, account.getUserId());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    public void testGetAccountByUserID_nonExistingAccount() {
        // Testing the scenario where no account exists for a given user ID
        // Mocking the behavior of querying for an account by user ID
        // Verifying that null is returned

        SqlRowSet mockResults = mock(SqlRowSet.class);
        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account WHERE user_id = ?", 123))
                .thenReturn(mockResults);
        when(mockResults.next()).thenReturn(false);

        Account account = accountDao.getAccountByUserID(123);
        assertEquals(null, account);
    }

    @Test(expected = DaoException.class)
    public void testGetAccountByUserID_cannotGetConnectionException() {
        // Testing the scenario where a CannotGetJdbcConnectionException is thrown
        // Verifying that a DaoException is thrown

        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account WHERE user_id = ?", 123))
                .thenThrow(new CannotGetJdbcConnectionException("Connection error"));

        accountDao.getAccountByUserID(123);
    }

    @Test
    public void testGetAccounts_multipleAccounts() {
        // Testing the scenario where multiple accounts are returned
        // Mocking the behavior of querying for all accounts
        // Verifying that the correct list of Account objects is returned

        SqlRowSet mockResults = mock(SqlRowSet.class);
        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account"))
                .thenReturn(mockResults);
        when(mockResults.next()).thenReturn(true, true, false); // Simulate multiple accounts
        when(mockResults.getInt("account_id")).thenReturn(1, 2);
        when(mockResults.getInt("user_id")).thenReturn(123, 456);
        when(mockResults.getBigDecimal("balance")).thenReturn(new BigDecimal("100.00"), new BigDecimal("200.00"));

//        // Mocking the mapRowToAccount method
//        when(accountDao.mapRowToAccount(mockResults)).thenReturn(new Account(1, 123, new BigDecimal("100.00")),
//                new Account(2, 456, new BigDecimal("200.00")));

        List<Account> accounts = accountDao.getAccounts();
        assertEquals(2, accounts.size());

        Account account1 = accounts.get(0);
        assertEquals(1, account1.getAccountId());
        assertEquals(123, account1.getUserId());
        assertEquals(new BigDecimal("100.00"), account1.getBalance());

        Account account2 = accounts.get(1);
        assertEquals(2, account2.getAccountId());
        assertEquals(456, account2.getUserId());
        assertEquals(new BigDecimal("200.00"), account2.getBalance());
    }

    @Test
    public void testGetAccounts_noAccounts() {
        // Testing the scenario where no accounts are returned
        // Mocking the behavior of querying for all accounts
        // Verifying that an empty list is returned

        SqlRowSet mockResults = mock(SqlRowSet.class);
        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account"))
                .thenReturn(mockResults);
        when(mockResults.next()).thenReturn(false); // Simulate no accounts

        List<Account> accounts = accountDao.getAccounts();
        assertEquals(0, accounts.size());
    }

    @Test(expected = DaoException.class)
    public void testGetAccounts_cannotGetConnectionException() {
        // Testing the scenario where a CannotGetJdbcConnectionException is thrown
        // Verifying that a DaoException is thrown

        when(jdbcTemplate.queryForRowSet("SELECT account_id, user_id, balance FROM account"))
                .thenThrow(new CannotGetJdbcConnectionException("Connection error"));

        accountDao.getAccounts();
    }
    @Test
    public void testGetBalanceByUserId_existingBalance() {
        // Testing the scenario where a balance exists for a given user ID
        // Mocking the behavior of querying for the balance by user ID
        // Verifying that the correct balance is returned

        SqlRowSet mockResults = mock(SqlRowSet.class);
        when(jdbcTemplate.queryForRowSet("SELECT balance FROM account WHERE user_id = ?", 123))
                .thenReturn(mockResults);
        when(mockResults.next()).thenReturn(true);
        when(mockResults.getBigDecimal("balance")).thenReturn(new BigDecimal("150.00"));

        BigDecimal balance = accountDao.getBalanceByUserId(123);
        assertEquals(new BigDecimal("150.00"), balance);
    }

    @Test
    public void testGetBalanceByUserId_nonExistingBalance() {
        // Testing the scenario where no balance exists for a given user ID
        // Mocking the behavior of querying for the balance by user ID
        // Verifying that null is returned

        SqlRowSet mockResults = mock(SqlRowSet.class);
        when(jdbcTemplate.queryForRowSet("SELECT balance FROM account WHERE user_id = ?", 456))
                .thenReturn(mockResults);
        when(mockResults.next()).thenReturn(false);

        BigDecimal balance = accountDao.getBalanceByUserId(456);
        assertNull(balance);
    }

    @Test(expected = DaoException.class)
    public void testGetBalanceByUserId_cannotGetConnectionException() {
        // Testing the scenario where a CannotGetJdbcConnectionException is thrown
        // Verifying that a DaoException is thrown

        when(jdbcTemplate.queryForRowSet("SELECT balance FROM account WHERE user_id = ?", 789))
                .thenThrow(new CannotGetJdbcConnectionException("Connection error"));

        accountDao.getBalanceByUserId(789);
    }


    // Add similar test methods for handling CannotGetJdbcConnectionException and DataIntegrityViolationException
}