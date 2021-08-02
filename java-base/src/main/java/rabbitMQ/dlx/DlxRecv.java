package rabbitMQ.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

public class DlxRecv {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        channel.basicQos(1);
        channel.basicConsume("dlx_queue", false, (s, delivery) -> {
                    String message = new String(delivery.getBody());
                    System.out.println("接收消息： " + message);
                    System.out.println("header is " + delivery.getProperties().getHeaders());
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                },
                s -> {
                });
    }
}
