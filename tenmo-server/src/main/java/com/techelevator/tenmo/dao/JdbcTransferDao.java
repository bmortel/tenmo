package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;

@Component
public class JdbcTransferDao implements TransferDao{

        private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        String sql = "Select * from transfer where transfer_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if(rowSet.next()){
            transfer = mapRowToTransfer(rowSet);
        }
        return transfer;
    }

    @Override
    public Transfer createTransfer(TransferDto transferdDto) {
        Transfer transfer = null;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
         try {
            int newUserId = jdbcTemplate.queryForObject(sql, int.class, transferdDto.getTransferTypeId(), transferdDto.getTransferStatusId(), transferdDto.getAccountFrom(), transferdDto.getAccountTo(), transferdDto.getAmount());
            transfer = getTransferById(newUserId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getPendingTransfers(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select * from transfer where transfer_status_id = 1 and account_to = ? ";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        while(rowSet.next()){
            transfers.add(mapRowToTransfer(rowSet));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransferByUser(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select * from transfer where (account_to = ? or account_from = ?)";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id, id);
        while(rowSet.next()){
            transfers.add(mapRowToTransfer(rowSet));
        }
        return transfers;
    }

    



    Transfer mapRowToTransfer(SqlRowSet rs){
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
