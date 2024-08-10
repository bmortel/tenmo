package com.techelevator.tenmo.dao;


import java.util.List;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {

    List<Account> getAccounts();

    Account getAccountByID(int id);

    Account getAccountByUserID(int id);

    Account updateAccountBalance(Account account);
}
