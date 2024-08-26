package com.techelevator.tenmo.model;


import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter   
public class Account {

    @NotNull
    private int accountId;
    @NotNull
    private int userId;
    @NotNull
    private BigDecimal balance;


}
