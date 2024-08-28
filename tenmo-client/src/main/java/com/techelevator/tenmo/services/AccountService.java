package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;

public class AccountService {

    private AuthenticatedUser currentUser;

    public static final String API_BASE_URL = "http://localhost:8080/accounts";
    private RestTemplate restTemplate = new RestTemplate();

    public void setCurrentUser(AuthenticatedUser user) {
        this.currentUser = user;
    }

    public Account getAccountById(int accountId) {
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(
                    API_BASE_URL + "/" + accountId,
                    HttpMethod.GET,
                    makeAuthEntity(),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }
    public Account getAccountByUserId(int userId) {
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(
                    API_BASE_URL + "/user/" + userId,
                    HttpMethod.GET,
                    makeAuthEntity(),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public BigDecimal getBalanceByUserId() {
        BigDecimal balance = null;
        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(
                    API_BASE_URL + "/user/" + currentUser.getUser().getId() + "/balance",
                    HttpMethod.GET,
                    makeAuthEntity(),
                    BigDecimal.class);
            balance = response.getBody();
            if (balance == null) {
                System.err.println("Error: Balance is null");
            } else {
                System.out.println("Balance retrieved: " + balance);
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public boolean updateAccountBalance(Account account) {
        HttpEntity<Account> entity = makeAccountEntity(account);
        boolean success = false;
        try {
            restTemplate.put(API_BASE_URL + "/" + account.getAccountId(), entity);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(account, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}
