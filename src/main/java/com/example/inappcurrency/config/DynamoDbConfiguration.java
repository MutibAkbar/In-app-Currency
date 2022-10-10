package com.example.inappcurrency.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class DynamoDbConfiguration {

    @Bean
    public DynamoDBMapper dynamoDBMapper(){
        return new DynamoDBMapper(buildAmazonDynamoDB());
    }

    /***
      Configuration of DynamoDB
     ***/
    private AmazonDynamoDB buildAmazonDynamoDB(){

        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties properties = new Properties();

            properties.load(input);

            return AmazonDynamoDBClientBuilder
                    .standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    properties.getProperty("serviceEndpoint"),
                                    properties.getProperty("signingRegion")
                            ))
                    .withCredentials(
                            new AWSStaticCredentialsProvider(
                                    new BasicAWSCredentials(
                                            properties.getProperty("accessKey"),
                                            properties.getProperty("secretKey")
                                    )
                            )).build();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
