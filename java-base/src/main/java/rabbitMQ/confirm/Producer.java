package rabbitMQ.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Producer {

    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();
        try {
            channel.queueDeclare("confirm_queue", false, false, false, null);
            //开启事务
            channel.txSelect();
            channel.basicPublish("", "confirm_queue_--1", null, "hello confirm queue".getBytes());
            //提交事务
            channel.txCommit();
            System.out.println("end");
            channel.close();
            connection.close();
        } catch (Exception e) {
            //事务回滚
            channel.txRollback();
            channel.close();
            connection.close();
        }
    }
}
