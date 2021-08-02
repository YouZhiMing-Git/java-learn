package rabbitMQ.work.polling;

import com.rabbitmq.client.*;

import java.io.IOException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Recv {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.0.172");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");


        String queueName = "work_queue";
        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, false, false, false, null);
           /* final DeliverCallback deliverCallback = new DeliverCallback() {
                @Override
                public void handle(String s, Delivery delivery) throws IOException {
                    final String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println("[1] Received " + message);
                    doWork(message);
                    //手动确认
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                }
            };*/
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            // 获取消息，并且处理，这个方法类似事件监听，如果有消息的时候，会被自动调用

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // body 即消息体
                String msg = new String(body);
                System.out.println(" [消费者1] received : " + msg + "!");
                doWork(msg);
                // 手动ACK
//                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(queueName, true, consumer);


    }

    public static void doWork(String msg) {
        final String[] msgs = msg.split("_");
        int time = Integer.parseInt(msgs[2]);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
