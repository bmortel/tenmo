package com.techelevator.tenmo.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountDao jdbcAccountDao;

    public AccountController(AccountDao accountDao) {
        this.jdbcAccountDao = accountDao;
    }

    @GetMapping
    public Account getAccountByUserId(@RequestParam int userID) {
       return  jdbcAccountDao.getAccountByUserID(userID);
    }
            

    @PutMapping("/{id}")
    public Account putMethodName(@PathVariable int id, @Valid @RequestBody Account account) {
        account.setAccountID(id);
        try {
            return jdbcAccountDao.updateAccountBalance(account);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        }

    }

}
