package com.youzm.helloworld;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Producer {
    public static void main(String[] args) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HelloWorldConfiguration.class);
        final RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);
       /* rabbitTemplate.setConfirmCallback((correlationData,  ack,  cause)->{
            System.out.println(correlationData);
            System.out.println("ack is "+ack);
            System.out.println("cause is "+cause);

        });*/
        rabbitTemplate.convertAndSend("hello world spring ampq");


    }
}
