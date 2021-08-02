package rabbitMQ.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

import java.util.HashMap;
import java.util.Map;

public class Recv {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        channel.basicConsume("test_queue", false, (s, delivery)->{
                   String message=new String(delivery.getBody()) ;
                    System.out.println("拒收消息： "+message);
                    //拒收消息
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                },
                s->{});
    }
}
