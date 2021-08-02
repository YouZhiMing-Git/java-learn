package rabbitMQ.topics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

public class Producer {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        String exchangeName="topic_logs";
        channel.exchangeDeclare(exchangeName,"topic");

        String message1="勇敢牛牛，不怕困难";
        String message2="牛奶真好喝";
        channel.basicPublish(exchangeName,"brave.cow",null,message1.getBytes());
        channel.basicPublish(exchangeName,"brave.cow.milk",null,message2.getBytes());
        channel.close();
        connection.close();

    }

}
