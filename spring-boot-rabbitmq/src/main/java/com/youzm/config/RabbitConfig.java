package com.youzm.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.Message;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Configuration
public class RabbitConfig {

    @Resource
    private ConnectionFactory connectionFactory;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("springboot_direct_exchange", true, false);
    }

    @Bean
    public Queue queueA() {
        return new Queue("direct_queue", true);
    }

    @Bean
    public Binding bindingA() {
        return BindingBuilder.bind(queueA()).to(exchange()).with("direct_Key");
    }

    //上面@Bean注解和下面的@RabbitListener都是可以自动声明绑定，交换机和队列的
    /**
     * 注解式消息消费及(交换机，队列)声明绑定
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue(value = "topic_queue",
                    durable = "true"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "springboot_topic_exchange",
                    durable = "true",
                    type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "springboot.*"
    )
    )
    @RabbitHandler
    public void comsumeB(Message message, Channel channel) throws IOException {
//        logger.info("RabbitListener-消费者B收到消息:{}", message.getPayload());
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        /**
         * 消费如果抛出了异常，处理方式:
         * 如果是手动签收模式，可以try catch包括，catch到了异常进行重回队列或者进行落库等操作
         * 如果是自动签收，默认会重回队列，然后一直循环重复消费。可以设置消息重新投递(设置最大投递次数，投递时间间隔，达到最大投递次数后是否重回队列等)
         */
        //模拟消费时抛出异常
//        int i=1/0;
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(queues = "direct_queue")
    @RabbitHandler
    public void comsumeA(@Payload Order order,
                         Channel channel,
                         @Headers Map<String, Object> headers) throws IOException {
//        logger.info("RabbitListener-消费者A收到消息:{}", order);
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
    }
}
