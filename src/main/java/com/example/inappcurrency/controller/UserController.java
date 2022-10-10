package com.example.inappcurrency.controller;

import com.example.inappcurrency.entity.User;
import com.example.inappcurrency.repository.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

 /***
 User endpoints for services
 ***/
@RestController
public class UserController {

    @Autowired
    private UserRepositoryInterface userRepositoryInterface;

    @PostMapping("/user")
    public User addUser(@RequestBody User user) throws Exception {
        return userRepositoryInterface.addUser(user);
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") String userId){
        return userRepositoryInterface.getUserById(userId);
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") String userId){
        return userRepositoryInterface.delete(userId);
    }


    @PutMapping("/user/addToWallet/{id}/amount={amount}")
    public User addMoneyToWallet(@PathVariable("id") String userId, @PathVariable("amount") int amount) throws Exception {
        return userRepositoryInterface.addMoneyToWallet(userId,amount);
    }

    @PutMapping("/user/payThroughWallet/{id}/amount={amount}")
    public User payThroughWallet(@PathVariable("id") String userId, @PathVariable("amount") int amount) throws Exception{
        return userRepositoryInterface.payThroughWallet(userId,amount);
    }


}
