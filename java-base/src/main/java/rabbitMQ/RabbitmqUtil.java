package rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitmqUtil {
    static ConnectionFactory connectionFactory=new ConnectionFactory();
    static Connection connection=null;

    public static Connection getConnection() throws IOException, TimeoutException {
        connectionFactory.setHost("192.168.0.172");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        connection= connectionFactory.newConnection();
        return connection;
    }

    public static Channel getChannel() throws IOException {
        return connection.createChannel();
    }

}
