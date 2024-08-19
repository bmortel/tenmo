package com.techelevator.tenmo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.service.TransferService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/{id}")
    public Transfer getTransferById(@PathVariable int id) {
        return transferService.getTransferById(id);
    }

    @GetMapping
    public List<Transfer> getTransfersByUserId(@RequestParam int userId) {
        return transferService.getTransfersByUserId(userId);
    }

    @GetMapping("/pending")
    public List<Transfer> getPendingTransfers(@RequestParam int accountId) {
        return transferService.getPendingTransfers(accountId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer) {
        return transferService.createTransfer(transfer);
    }

    @PutMapping("/{id}")
    public Transfer updateTransfer(@PathVariable int id, @Valid @RequestBody Transfer transfer) {
        return transferService.updateTransfer(id, transfer);
    }

}
