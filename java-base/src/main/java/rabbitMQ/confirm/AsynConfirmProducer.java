package rabbitMQ.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import rabbitMQ.RabbitmqUtil;

import java.io.IOException;
import java.util.TreeSet;

public class AsynConfirmProducer {

    public static void main(String[] args) throws Exception {

        //用于保存消息发送的序号
        final TreeSet<Long> confirmSet = new TreeSet<>();

        final Connection connection = RabbitmqUtil.getConnection();
        final Channel channel = RabbitmqUtil.getChannel();

        channel.queueDeclare("confirm_queue", false, false, false, null);
        //开启confirm模式
        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                //处理成功的消息
                if(multiple){
                    //批量
                    System.out.println("ack批量确认,deliveryTag:"+deliveryTag+",multiple:"+multiple+"," +
                            "当次确认消息序号集合:"+confirmSet.headSet(deliveryTag + 1));
                    confirmSet.headSet(deliveryTag+1).clear();//清除
                }else{
                    System.out.println("ack批量确认,deliveryTag:"+deliveryTag+",multiple:"+multiple+"," +
                            "当次确认消息序号:"+deliveryTag);
                    confirmSet.remove(deliveryTag);//清除
                }
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                //处理失败的消息
                if(multiple){
                    //批量
                    confirmSet.headSet(deliveryTag + 1).clear();
                }else{
                    confirmSet.remove(deliveryTag);
                }
            }
        });

        for (int i = 0; i <50 ; i++) {
            String message="hello confirm queue "+i;
            //获取下一个消息发送的序号
            final long nextPublishSeqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("", "confirm_queue", null, message.getBytes());
            //将序号存入集合中
            confirmSet.add(nextPublishSeqNo);
        }

       /* channel.close();
        connection.close();*/


    }
}
