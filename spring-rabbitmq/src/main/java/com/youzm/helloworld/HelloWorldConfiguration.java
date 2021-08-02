package com.youzm.helloworld;



import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HelloWorldConfiguration {
    String EXCHANGE_NAME="hello.spring.exchange";
    String QUEUE_NAME="hello.spring.queue";
    //连接工厂
    @Bean
    public ConnectionFactory connectionFactory(){
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("192.168.0.172");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }
    //这是一个系统管理组件,用它来创建消息队列等等
    @Bean
    public AmqpAdmin amqpAdmin(){
        RabbitAdmin rabbitAdmin= new RabbitAdmin(connectionFactory());
        rabbitAdmin.declareExchange(exchange());//声明交换机
        rabbitAdmin.declareQueue(queue());//声明队列
        rabbitAdmin.declareBinding(binding());//声明绑定关系
        return rabbitAdmin;
    }


    //创建rabbitmq客户端
    @Bean
    public RabbitTemplate rabbitTemplate(){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setEncoding("utf-8");
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        rabbitTemplate.setQueue(QUEUE_NAME);
        rabbitTemplate.setRoutingKey("spring");
        rabbitTemplate.setConfirmCallback(producerHandler());
        System.out.println(rabbitTemplate.isConfirmListener());
        return rabbitTemplate;

    }

    @Bean
    public Exchange exchange(){
        final CustomExchange exchange = new CustomExchange(EXCHANGE_NAME,"direct",false,false,null);

        final DirectExchange directExchange = new DirectExchange(EXCHANGE_NAME);
        final TopicExchange topicExchange = new TopicExchange(EXCHANGE_NAME);
        final FanoutExchange fanoutExchange = new FanoutExchange(EXCHANGE_NAME);
        //。。。
        return exchange;

    }
    @Bean
    public  Queue queue(){
        final Queue queue = new Queue(QUEUE_NAME,false,false,false,null);
        return queue;
    }

    @Bean
    public Binding binding(){
        final Binding binding = new Binding(QUEUE_NAME, Binding.DestinationType.QUEUE,EXCHANGE_NAME,"#",null);
        return binding;
    }
    @Bean
    public ProducerHandler producerHandler(){
        return new ProducerHandler();
    }
    @Bean
    public ConsumeHandler consumeHandler(){
        return new ConsumeHandler();
    }


    /*@Bean
    public SimpleMessageListenerContainer listenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(this.QUEUE_NAME);
        container.setMessageListener(consumeHandler());
        return container;
    }*/
}
