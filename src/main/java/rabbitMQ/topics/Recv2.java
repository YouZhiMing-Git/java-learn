package rabbitMQ.topics;

import com.rabbitmq.client.*;
import rabbitMQ.RabbitmqUtil;

import java.io.IOException;

public class Recv2 {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        String exchangeName = "topic_logs";
        String queueName = "logs_queue2";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName,exchangeName,"#.cow.*");

        channel.basicConsume(queueName,false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
               String message=new String(body);
                System.out.println("消费者[2] 接收消息："+message);
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });
      /*  channel.close();
        connection.close();*/

    }

}
