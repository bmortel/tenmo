// This class represents a service for interacting with accounts in the system
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

// AccountService class definition
public class AccountService {

    // Instance variable to store the currently authenticated user
    private AuthenticatedUser currentUser;

    // Base URL for the API
    public static final String API_BASE_URL = "http://localhost:8080/accounts";
    private RestTemplate restTemplate = new RestTemplate();

    // Method to set the current authenticated user
    public void setCurrentUser(AuthenticatedUser user) {
        this.currentUser = user;
    }

    // Method to get an account by its ID
    public Account getAccountById(int accountId) {
        Account account = null;
        try {
            // Make a GET request to the API to retrieve the account
            ResponseEntity<Account> response = restTemplate.exchange(
                    API_BASE_URL + "/" + accountId,
                    HttpMethod.GET,
                    makeAuthEntity(),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            // Log any exceptions that occur during the request
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    // Method to get an account by user ID
    public Account getAccountByUserId(int userId) {
        Account account = null;
        try {
            // Make a GET request to the API to retrieve the account by user ID
            ResponseEntity<Account> response = restTemplate.exchange(
                    API_BASE_URL + "/user/" + userId,
                    HttpMethod.GET,
                    makeAuthEntity(),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            // Log any exceptions that occur during the request
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    // Method to get the balance for the current user
    public BigDecimal getBalanceByUserId() {
        BigDecimal balance = null;
        try {
            // Make a GET request to the API to retrieve the balance for the current user
            ResponseEntity<BigDecimal> response = restTemplate.exchange(
                    API_BASE_URL + "/user/" + currentUser.getUser().getId() + "/balance",
                    HttpMethod.GET,
                    makeAuthEntity(),
                    BigDecimal.class);
            balance = response.getBody();
            // if (balance == null) {
            // System.err.println("Error: Balance is null");
            // } else {
            // System.out.println("Balance retrieved: " + balance);
            // }
        } catch (RestClientResponseException | ResourceAccessException e) {
            // Log any exceptions that occur during the request
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    // Method to update the balance of an account
    public boolean updateAccountBalance(Account account) {
        // Create an HttpEntity with the updated account information
        HttpEntity<Account> entity = makeAccountEntity(account);
        boolean success = false;
        try {
            // Make a PUT request to update the account balance
            restTemplate.put(API_BASE_URL + "/" + account.getAccountId(), entity);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            // Log any exceptions that occur during the request
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    // Helper method to create an HttpEntity for an Account object
    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(account, headers);
    }

    // Helper method to create an HttpEntity for authentication
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }
}