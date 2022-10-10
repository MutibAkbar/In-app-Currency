package com.example.inappcurrency.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public class Wallet {

    @DynamoDBAttribute
    private String walletAmount;

    public String getWalletAmount() { return walletAmount; }
    public void setWalletAmount(String walletAmount) { this.walletAmount = walletAmount; }

}
