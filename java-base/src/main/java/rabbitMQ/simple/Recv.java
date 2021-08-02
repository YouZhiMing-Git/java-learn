package rabbitMQ.simple;

import com.rabbitmq.client.*;

import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Recv {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("192.168.0.172");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");


        Connection recvConnection = connectionFactory.newConnection("recv");
        Channel channel = recvConnection.createChannel();


        channel.queueDeclare("test_queue", true, false, false, null);
        channel.basicQos(1);//一次接收一条
        System.out.println("接收消息");
        channel.basicConsume("test_queue", false, (s, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("接收到消息" + message);
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(delivery.getProperties().getHeaders().get("number")=="1"){
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            }
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        }, consumerTag -> {
        });


    }
}
