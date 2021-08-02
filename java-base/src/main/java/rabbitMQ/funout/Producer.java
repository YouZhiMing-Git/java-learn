package rabbitMQ.funout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

public class Producer {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        String exchangeName="logs";
        channel.exchangeDeclare(exchangeName,"fanout");
        channel.exchangeDeclare(exchangeName,"fanout",true);
        String message="info: Hello world";
        channel.basicPublish(exchangeName,"",null,message.getBytes());
        channel.close();
        connection.close();

    }

}
