package rabbitMQ.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static void main(String[] args) {
        /***
         * 1 创建连接工程
         * 2 创建连接Connection
         * 3 通过连接获取通道
         * 4 通过连接创建交换机，申明队列，绑定关系，路由可以，发送消息和接收消息
         * 5 准备消息
         * 6 发送消息
         * 7 关闭连接
         * 8 关闭通道
         */

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.0.172");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection("生产者");
            channel = connection.createChannel();
            String queueName = "queue";
            /***
             * @param1 队列名称
             * @param2 是否持久化（false 非持久化，true 持久化）
             * @param3 排他性 是否是独占
             * @param4 是否自动删除  随着最后一个消费者消息完毕以后是否吧队列自动删除
             * @param5 携带附属参数
             */
            channel.queueDeclare(queueName, false, false, false, null);
            String message = "Hello world";
            channel.basicPublish("", queueName, null, message.getBytes());

            System.out.println("message has been send");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            if(channel!=null && channel.isOpen()){
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null && connection.isOpen()){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
