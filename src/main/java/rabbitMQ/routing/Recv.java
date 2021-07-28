package rabbitMQ.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("192.168.0.172");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");


        Connection recvConnection=null;
        Channel channel=null;
        try {
            recvConnection = connectionFactory.newConnection("recv");
            channel=recvConnection.createChannel();

            channel.queueDeclare("queue",false,false,false,null);
            System.out.println("接收消息");

            channel.basicConsume("queue",true,( s,  delivery)->{
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("接收到消息"+message);
            },consumerTag->{});



        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
