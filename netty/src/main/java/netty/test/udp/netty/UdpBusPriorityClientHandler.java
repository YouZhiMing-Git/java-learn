package netty.test.udp.netty;

import com.ehualu.eloc.common.frame.SpringContextUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;


/**
 * Netty UDP Client Handler
 *
 * @author Adam
 * @Date 2020/1/8
 */
@Slf4j
public class UdpBusPriorityClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private UdpBusPriorityProtocol nettyServerProtocol = SpringContextUtils.getBean(UdpBusPriorityProtocol.class);
//    private SystemComponent systemComponent = SpringContextUtils.getBean(SystemComponent.class);
    /**
     * client 对象
     **/
    private UdpBusPriorityClient udpClient;
    /**
     * 路口编号
     **/
    private String crossNo;

    /**
     * 构造函数
     *
     * @param udpClient
     */
    public UdpBusPriorityClientHandler(UdpBusPriorityClient udpClient) {
        super();
        this.udpClient = udpClient;
        crossNo = udpClient.getCrossNo();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        boolean active = channel.isActive();
//        String ipAddress = channel.remoteAddress().toString();
//        log.info("[" + getIp(ipAddress) + "] is online--"+active);
//        log.info(" is online--" + active + "," + udpClient.toString());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error(ctx.channel() + "断开！");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(ctx.channel() + "异常断开断开！");
//        ctx.close();
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chx, DatagramPacket datagramPacket) throws Exception {
        Channel channel = chx.channel();
        ByteBuf byteBuf = datagramPacket.content();

        //处理收到的协议
        nettyServerProtocol.transferProtocol(crossNo, byteBuf);

    }

    /**
     * 获取IP地址
     *
     * @param ip
     * @return
     */
    private String getIp(String ip) {
        ip = ip.substring(1);
        return ip.split(":")[0];
    }


}
