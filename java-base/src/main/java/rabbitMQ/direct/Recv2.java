package rabbitMQ.direct;

import com.rabbitmq.client.*;
import rabbitMQ.RabbitmqUtil;

import java.io.IOException;

public class Recv2 {
    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        String exchangeName = "direct_logs";
        String queueName = "logs_queue2";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName,exchangeName,"info");
        channel.queueBind(queueName,exchangeName,"warn");
        channel.queueBind(queueName,exchangeName,"error");

        channel.basicConsume(queueName,false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
               String message=new String(body);
                System.out.println("消费者[2] 接收消息："+message);
            }
        });
      /*  channel.close();
        connection.close();*/

    }

}
