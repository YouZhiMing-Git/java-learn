package netty.test.udp.netty;

import com.ehualu.eloc.common.frame.SpringContextUtils;
import com.ehualu.eloc.sts.system.SystemUdpComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * @author:youzhiming
 * @date: 2023/6/28
 * @description:
 */
@Slf4j
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private NettyProtocol protocol = SpringContextUtils.getBean(NettyProtocol.class);
    private SystemUdpComponent systemUdpComponent = SpringContextUtils.getBean(SystemUdpComponent.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        InetAddress address = msg.sender().getAddress();
        ByteBuf byteBuf = msg.content();
        String crossNo = systemUdpComponent.getIpGroup().get(address.toString().substring(1));
        if(crossNo==null){
            log.info("ip:{} 无预加载，当前ipGroup:{}",address,systemUdpComponent.getIpGroup());
            return;
        }
        //处理收到的协议
        protocol.transferProtocol(crossNo, byteBuf);
    }
}
