package rabbitMQ.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

public class ConfirmProducer {

    public static void main(String[] args) throws Exception {


        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();

        channel.queueDeclare("confirm_queue", false, false, false, null);
        //开启confirm模式
        channel.confirmSelect();
        channel.basicPublish("", "confirm_queue", null, "hello confirm queueu".getBytes());
        if(!channel.waitForConfirms()){
            System.out.println("投递失败");
        }
        System.out.println("投递结束");

        channel.close();
        connection.close();


    }
}
