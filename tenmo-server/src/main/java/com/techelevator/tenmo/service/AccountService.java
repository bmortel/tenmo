package com.techelevator.tenmo.service;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountService {

    //TODO Add DaoException catch to rest of methods

    private final AccountDao jdbcAccountDao;
    private final UserDao userDao;

    public List<Account> getAllAccounts() {
        return jdbcAccountDao.getAccounts();
    }

    public Account getAccountById(@PathVariable int accountId){
        return jdbcAccountDao.getAccountByID(accountId);
    }

    public Account getAccountByUserId(@RequestParam int userId) {
        return jdbcAccountDao.getAccountByUserID(userId);
    }

    public Account updateAccountBalance(@PathVariable int id, @Valid @RequestBody Account account) {
        account.setAccountId(id);
        try {
            return jdbcAccountDao.updateAccountBalance(account);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        }

    }

    public BigDecimal getBalanceByUserId(@PathVariable int userId) {
        BigDecimal balance = jdbcAccountDao.getBalanceByUserId(userId);
        if (balance != null) {
            return new ResponseEntity<>(balance, HttpStatus.OK).getBody();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Balance not found");
        }
    }
}