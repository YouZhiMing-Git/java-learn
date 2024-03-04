package netty.test.server.netty;

import com.ehualu.eloc.common.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Adam
 * @Date 2021/6/17 15:24
 * @Description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NettyServerSrc {
    private String slcIp;
    private List<String> crossNoList;
    private Channel channel;
    //协议接收最新时间
    private int receiveTime;

    /*
     * @Author szx
     * @Description 发送消息方法
     * @Date 10:58 2020/12/25
     * @Param [b]
     * @return void
     **/
    public void sendMessage(byte[] b) {
        if (channel == null) return;
        if (!channel.isActive()) return;
        if (crossNoList == null) return;
        ByteBuf buffer = channel.alloc().buffer(b.length);
        buffer.writeBytes(b);
        channel.writeAndFlush(buffer);
        for (String crossNo : crossNoList) {
            String s = CommonUtil.byte2String(b);
            if (!s.equals("7E,00,0C,10,00,01,00,00,00,01,01,00,70,FA,92,7D")){
//                System.out.println("发送：" + crossNo + "  " + s);
            }
            //输出日志

//            log.info("接收： " + slcIp + "  " + CommonUtil.byte2String(bTemp));
        }
    }

}
