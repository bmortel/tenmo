package com.techelevator.tenmo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TransferService {

    private final JdbcTransferDao transferDao;


    public Transfer getTransferById(int transferId) {
        Transfer transfer = transferDao.getTransferById(transferId);
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return transfer;
        }
    }

    public List<Transfer> getTransfersByUserId(int userId) {
        return transferDao.getTransfersByUserId(userId);
    }

    public List<Transfer> getPendingTransfers(int accountId){
        return transferDao.getPendingTransfers(accountId);
    }

    public Transfer createTransfer(Transfer transfer) {
        return transferDao.createTransfer(transfer);
    }

    public Transfer updateTransfer(int transferId, Transfer transfer) {
        transfer.setTransferId(transferId);
        try {
            return transferDao.updateTransfer(transfer);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
