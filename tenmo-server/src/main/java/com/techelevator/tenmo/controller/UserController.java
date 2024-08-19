package com.techelevator.tenmo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{accountId}")
    public String getUserByAccountId(@PathVariable int accountId) {
        return userService.getUserByAccountId(accountId);
    }
    
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }
    
}
