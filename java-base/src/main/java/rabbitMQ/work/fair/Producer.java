package rabbitMQ.work.fair;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.0.172");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");

        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()) {

            String queueName="work_queue";
            String exchangeName="work_exchange";

            channel.exchangeDeclare(exchangeName,"direct");
            channel.queueDeclare(queueName,false,false,false,null);
            for(int i=0;i<50;i++){
                String msg="hello workQueue_tianya_"+i;
                channel.basicPublish("",queueName,false,false,null,msg.getBytes());
            }

            System.out.println("msg has been send!");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {

        }

    }
}
