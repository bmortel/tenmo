package com.techelevator.tenmo.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Transfer {

    @NotNull
    private int transferId;
    @NotNull
    private int transferTypeId;
    @NotNull
    private int transferStatusId;
    @NotNull
    private int accountFrom;
    @NotNull
    private int accountTo;
    @NotNull
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer(@NotNull int transferTypeId, @NotNull int transferStatusId, @NotNull int accountFrom,
            @NotNull int accountTo, @NotNull BigDecimal amount) {
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

}
