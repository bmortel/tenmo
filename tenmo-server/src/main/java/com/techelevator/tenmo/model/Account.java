package com.techelevator.tenmo.model;


import javax.validation.constraints.NotBlank;

public class Account {
    
    @NotBlank
    private int accountID;
    @NotBlank
    private int userID;
    @NotBlank
    private double balance;

    public int getAccountID() {
        return accountID;
    }
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
