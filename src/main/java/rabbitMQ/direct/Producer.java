package rabbitMQ.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

public class Producer {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        String exchangeName="direct_logs";
        channel.exchangeDeclare(exchangeName,"direct");

        String message="info: Hello world";
        String message1="warn: Hello world";
        String message2="error: Hello world";
        String message3="info: Hello world=============";
        channel.basicPublish(exchangeName,"info",null,message.getBytes());
        channel.basicPublish(exchangeName,"warn",null,message1.getBytes());
        channel.basicPublish(exchangeName,"error",null,message2.getBytes());
        channel.basicPublish(exchangeName,"info",null,message3.getBytes());


        channel.close();
        connection.close();

    }

}
