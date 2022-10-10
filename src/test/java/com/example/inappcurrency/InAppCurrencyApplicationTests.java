package com.example.inappcurrency;

import com.example.inappcurrency.entity.User;
import com.example.inappcurrency.entity.Wallet;
import com.example.inappcurrency.repository.UserRepositoryInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class InAppCurrencyApplicationTests {

    @Autowired
    private UserRepositoryInterface userRepositoryInterface;

    private User createUser(String role) throws Exception {
        User user = new User();
        Wallet wallet = new Wallet();
        user.setEmail("revakbar@gmail.com");
        user.setUserName("Reva");
        user.setPassword("hello123");
        wallet.setWalletAmount("2000");
        user.setWallet(wallet);
        user.setRole(role);
        return userRepositoryInterface.addUser(user);
    }
    @Test
    void addUserToDynamoDBSuccessfully() throws Exception {
        User newUser = createUser("user");
        Assertions.assertSame(newUser, newUser);
    }

    @Test
    void addUserToDynamoDBNotSuccessfully() {
        User user = new User();
        Exception thrown = assertThrows(
                Exception.class,
                () -> userRepositoryInterface.addUser(user),
                "User doesn't added successfully!"
        );
        assertTrue(thrown.getMessage().contains("User doesn't added successfully!"));
    }

    @Test
    void addMoneyToWalletSuccessfully() throws Exception {
        User user = userRepositoryInterface.getUserById("79b44bff-f4ef-42ec-bf41-76511554c916");
        int amountToBeAdded = 500;
        Assertions.assertEquals(Integer.valueOf(userRepositoryInterface.addMoneyToWallet(user.getUserId(), amountToBeAdded).getWallet().getWalletAmount()),  Integer.valueOf(user.getWallet().getWalletAmount())+amountToBeAdded);
    }

    @Test
    void addMoneyToWalletWithUnauthorizedUser() throws Exception {
        User adminUser = createUser("admin");
        Exception thrown = assertThrows(
                Exception.class,
                () -> userRepositoryInterface.addMoneyToWallet(adminUser.getUserId(),500),
                "Unauthorised access OR Invalid amount"
        );
        assertTrue(thrown.getMessage().contains("Unauthorised access OR Invalid amount"));
    }

    @Test
    void addMoneyToWalletWithIInvalidAmount() throws Exception {
        User adminUser = createUser("user");
        Exception thrown = assertThrows(
                Exception.class,
                () -> userRepositoryInterface.addMoneyToWallet(adminUser.getUserId(),-50),
                "Unauthorised access OR Invalid amount"
        );
        assertTrue(thrown.getMessage().contains("Unauthorised access OR Invalid amount"));

    }

    @Test
    void payThroughWalletSuccessfully() throws Exception {
        User user = userRepositoryInterface.getUserById("79b44bff-f4ef-42ec-bf41-76511554c916");
        int amountToBeAdded = 100;
        Assertions.assertEquals(Integer.valueOf(userRepositoryInterface.payThroughWallet(user.getUserId(), amountToBeAdded).getWallet().getWalletAmount()),  Integer.valueOf(user.getWallet().getWalletAmount())-amountToBeAdded);
    }

    @Test
    void payThroughWalletWithUnauthorizedUser() throws Exception {
        User adminUser  = createUser("admin");
        Exception thrown = assertThrows(
                Exception.class,
                () -> userRepositoryInterface.payThroughWallet(adminUser.getUserId(),100),
                "Unauthorised access OR Not valid amount"
        );
        assertTrue(thrown.getMessage().contains("Unauthorised access OR Not valid amount"));
    }

    @Test
    void payThroughWalletWithInvalidAmount() throws Exception {
        User adminUser = createUser("user");
        Exception thrown = assertThrows(
                Exception.class,
                () -> userRepositoryInterface.payThroughWallet(adminUser.getUserId(),-100),
                "Unauthorised access OR Not valid amount"
        );
        assertTrue(thrown.getMessage().contains("Unauthorised access OR Not valid amount"));
    }

    @Test
    void payThroughWalletWithNoAmount() throws Exception {
        User adminUser = createUser("user");
        Exception thrown = assertThrows(
                Exception.class,
                () -> userRepositoryInterface.payThroughWallet(adminUser.getUserId(),5000),
                "Insufficient Balance!"
        );
        assertTrue(thrown.getMessage().contains("Insufficient Balance!"));
    }
}


