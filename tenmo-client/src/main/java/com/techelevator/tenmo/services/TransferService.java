package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;

public class TransferService {

    private AuthenticatedUser currentUser;

    public void setAuthUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    public static final String API_BASE_URL = "http://localhost:8080/transfers";
    private RestTemplate restTemplate = new RestTemplate();

    //Fetches a transfer from the API by its ID.
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String url = API_BASE_URL + "/" + transferId;
        try {
        //TODO GET method
            ResponseEntity<Transfer> response = restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    //Fetches a list of transfers for a given user ID from the API.
    public Transfer[] getTransfersByUserId(int userId) {
        Transfer[] transfers = null;
        String url = API_BASE_URL + "?userId=" + userId;
        try {
        //TODO GET method
            ResponseEntity<Transfer[]> response = restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.err.println(e);
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    // Fetches a list of pending transfers for a given account ID from the API.
    public Transfer[] getPendingTransfers(int accountId) {
        Transfer[] transfers = null;
        String url = API_BASE_URL + "/pending?accountId=" + accountId;
        try {
        //TODO GET method
            ResponseEntity<Transfer[]> response = restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    //Sends a transfer to the API for creation.
    public Transfer createTransfer(Transfer newTransfer) {
        HttpEntity<Transfer> entity = makeTransferEntity(newTransfer);
        Transfer returnedTransfer = null;
        String url = API_BASE_URL;
        try {
        //TODO hint:post for object
            ResponseEntity<Transfer> response = restTemplate.exchange(url, HttpMethod.POST, entity, Transfer.class);
            returnedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

    // Updates a transfer on the API.
    public boolean updateTransfer(Transfer transfer) {
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        boolean success = false;

        String url = API_BASE_URL + "/" + transfer.getTransferId();
        try {
        //TODO PUT method
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("UPDATE TRANSFER ERROR");
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    // Created an httpEntity for a transfer request, including authentication headers.
    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

    // Creates an HttpEntity with authentication headers only.
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }

}
