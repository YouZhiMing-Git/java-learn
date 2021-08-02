package com.youzm.helloworld;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Recv {
    public static void main(String[] args) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HelloWorldConfiguration.class);
        final RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);

        System.out.println(rabbitTemplate.receiveAndConvert());
    }
}
