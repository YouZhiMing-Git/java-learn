package com.youzm.helloworld;


import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class HelloWorldConfiguration {

    @Bean
    public ConnectionFactory connectionFactory(){
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("");
        return connectionFactory;
    }

}
