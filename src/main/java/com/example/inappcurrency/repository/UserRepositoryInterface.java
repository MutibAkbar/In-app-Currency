package com.example.inappcurrency.repository;

import com.example.inappcurrency.entity.User;

/***
 UserRepository Interface
 ***/
public interface UserRepositoryInterface {
    User addUser(User user) throws Exception;

    User getUserById(String userId);

    String delete(String userId);

    User addMoneyToWallet(String userId, int addAmount) throws Exception;

    User payThroughWallet(String userId, int payAmount) throws Exception;
}
