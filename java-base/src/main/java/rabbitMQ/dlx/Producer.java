package rabbitMQ.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import rabbitMQ.RabbitmqUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();


        //===========================声明死信队列=================================
        String queueName = "dlx_queue";
        String exchangeName = "dlx_exchange";
        //声明交换机
        channel.exchangeDeclare(exchangeName, "topic");
        //声明队列
        Map map = new HashMap<String, Object>();
        map.put("x-dead-letter-exchange", "");//设置队列为死信队列
        channel.queueDeclare(queueName, false, false, false, map);
        //绑定死信队列
        channel.queueBind(queueName, exchangeName, "#");


        //==========================声明普通队列与死信队列绑定关系============================
        Map argument = new HashMap<String, Object>();
        argument.put("x-dead-letter-exchange", exchangeName);//绑定死信队列交换机
        argument.put("x-dead-letter-routing-key", "dlx.test");//绑定消息路由key
        argument.put("x-message-ttl",10000);//设置过期时间
        channel.queueDeclare("test_queue", true, false, false, argument);


        String message="hello dlx message 2";
        channel.basicPublish("", "test_queue", null,
                message.getBytes());

        System.out.println("message has been send");

        channel.close();
        connection.close();

    }
}
