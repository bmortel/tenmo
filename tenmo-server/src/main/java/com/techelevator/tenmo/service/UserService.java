package com.techelevator.tenmo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JdbcUserDao jdbcUserDao;

    public String getUserByAccountId(int accountId){
        return jdbcUserDao.getUserByAccountId(accountId);
    }

    public List<User> getUsers(){
        return jdbcUserDao.getUsers();
    }
}
