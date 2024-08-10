package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;

public interface TransferDao {

    List<Transfer> getTransferByUser(int id);

    Transfer getTransferById(int id);

    Transfer createTransfer(TransferDto transfer);

    List<Transfer> getPendingTransfers(int id);

}
