package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        //TODO get transfer by transfer_id
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByUserId(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        //TODO get list of transfers where the either "account_to" or "account_from" is the account_id of the user_id parameter
        return transfers;
    }

    @Override
    public List<Transfer> getPendingTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        //TODO get list of transfers where the transfer status is pending and account_from is current user's id
        return transfers;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer newTransfer = null;
        //TODO
        try {
        //TODO hint:query for object
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newTransfer;
    }

    @Override
    public Transfer updateTransfer(Transfer transfer) {
        Transfer updatedTransfer = null;
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, transfer.getTransferStatusId(), transfer.getTransferId());
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                updatedTransfer = getTransferById(transfer.getTransferId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedTransfer;
    }

    Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }

}
