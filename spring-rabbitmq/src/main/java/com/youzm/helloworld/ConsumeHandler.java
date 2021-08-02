package com.youzm.helloworld;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

public class ConsumeHandler  implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //接收消息回调方法

        System.out.println("接收消息："+message.toString());

        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);//确认签收

    }
}
