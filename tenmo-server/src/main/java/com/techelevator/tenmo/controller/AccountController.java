// Controller for managing accounts
package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import com.techelevator.tenmo.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// Ensures that only authenticated users can access these endpoints
@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final TokenProvider tokenProvider;
    private final AccountService accountService;

    // Get account details by account id
    @GetMapping("/{accountId}")
    public Account getAccountById(@PathVariable int accountId) {
        return accountService.getAccountById(accountId);
    }

    // Get account details by user id as a path variable
    @GetMapping("/user/{userId}")
    public Account getAccountByUserId(@PathVariable int userId) {
        return  accountService.getAccountByUserId(userId);
    }

    // Get details of all accounts
    @GetMapping("/all")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // Get balance of an account by user id
    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalanceByUserId(@PathVariable int userId, @RequestHeader ("Authorization") String token) {
        BigDecimal balance = accountService.getBalanceByUserId(userId, findUser(token));
        if (balance != null) {
            //System.out.println("Balance retrieved for user " + userId + ": " + balance);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } else {
            //System.err.println("Balance not found for user " + userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update account balance
    @PutMapping("/{id}")
    public Account updateAccountBalance(@PathVariable int id, @Valid @RequestBody Account account) {
        return accountService.updateAccountBalance(id, account);
    }

    // Test method to retrieve user details
    @GetMapping("/test")
    public String getMethodName(@RequestHeader("Authorization") String token) {
        return findUser(token);
    }

    // Extract username from token
    private String findUser(String token){
        String jwt = token.substring(7);
        return tokenProvider.getUsername(jwt);
    }

}