package com.example.inappcurrency.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.inappcurrency.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 User Business Logic
 ***/
@Repository
public class UserRepository implements UserRepositoryInterface {

    /***
     Handles exceptions with local error messages
     ***/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getLocalizedMessage());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private boolean checkIfUserIsNotNull(User user){
        return (user.getUserName() != null && user.getRole() != null && user.getEmail() != null && user.getPassword() != null && user.getWallet().getWalletAmount() != null);
    }

    /***
     Add user to DynamoDB with userId as PrimaryKey
     ***/
    @Override
    public User addUser(User user) throws Exception {
        if (checkIfUserIsNotNull(user)){
            dynamoDBMapper.save((user));
            return user;
        }
        throw new Exception("User doesn't added successfully!");
    }

    /***
     Get User data from DynamoDB using specific userID
     ***/
    @Override
    public User getUserById(String userId){
        return dynamoDBMapper.load(User.class, userId);
    }

    /***
     Delete user from DynamoDB
     ***/
    @Override
    public String delete(String userId){
        User user = dynamoDBMapper.load(User.class, userId);
        dynamoDBMapper.delete(user);
        return "Employee Deleted";
    }

    private boolean isUserIdsMatched(User user, String userId){
        return user.getUserId().equals(userId);
    }

    private boolean isUserAuthorizedAndAmountToBeAddedValid(User user, int amount){
        return ((!user.getRole().equals("admin")) && amount>0);
    }

    /***
     Update amount of user wallet in DynamoDB based on add and pay method
     ***/
    private User updateWallet(User user, String userId, String walletOption, int amount){
        Integer existingUserWallet = Integer.valueOf(user.getWallet().getWalletAmount());

        if(walletOption.equals("add"))
            user.getWallet().setWalletAmount(String.valueOf(existingUserWallet + amount));
        else
            user.getWallet().setWalletAmount(String.valueOf(existingUserWallet - amount));

        dynamoDBMapper.save(user,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("userId", new ExpectedAttributeValue(
                                new AttributeValue().withS(userId)
                        ))
        );
        LOGGER.log(Level.INFO, "Amount added to the requested user's wallet successfully");
        return user;
    }

    /***
     Add money to wallet if required conditions are satisfied
     ***/
    @Override
    public User addMoneyToWallet(String userId, int addAmount) throws Exception {
        User existingUser = getUserById(userId);
        if(isUserIdsMatched(existingUser, userId)){
            if (isUserAuthorizedAndAmountToBeAddedValid(existingUser, addAmount)) {
                return updateWallet(existingUser, userId, "add",addAmount);
            }
            throw new Exception("Unauthorised access OR Invalid amount");
        }
        throw new Exception("Requested User doesn't exist");
    }

    private boolean isPaymentAmountLessThanTotalAmount(User user, int amount){
        return (Integer.valueOf(user.getWallet().getWalletAmount()) < amount);
    }

    /***
     Pay money from wallet if required conditions are satisfied
     ***/
    @Override
    public User payThroughWallet(String userId, int payAmount) throws Exception{
        User existingUser = getUserById(userId);
        if(isUserIdsMatched(existingUser, userId)){
            if(isPaymentAmountLessThanTotalAmount(existingUser, payAmount)){
                throw new Exception("Insufficient Balance!");
            }
            if (isUserAuthorizedAndAmountToBeAddedValid(existingUser, payAmount)) {
                return updateWallet(existingUser, userId, "subtract", payAmount);
            }
            throw new Exception("Unauthorised access OR Not valid amount");
        }
        throw new Exception("Requested User doesn't exist");
    }
}
