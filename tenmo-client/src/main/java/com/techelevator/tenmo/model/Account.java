package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import lombok.*;

@Data
public class Account {

    private int accountId;
    private int userId;
    private BigDecimal balance;

}
