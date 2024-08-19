package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao {

    Transfer getTransferById(int id);

    List<Transfer> getTransfersByUserId(int id);

    List<Transfer> getPendingTransfers(int id);

    Transfer createTransfer(Transfer transfer);

    Transfer updateTransfer(Transfer transfer);

}
