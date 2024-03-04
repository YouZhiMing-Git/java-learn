package netty.test.udp.netty;

import com.ehualu.eloc.common.frame.SpringContextUtils;
import com.ehualu.eloc.common.infrastructure.redis.RedisUtil;
import com.ehualu.eloc.common.protocolhaixin.send.*;
import com.ehualu.eloc.common.util.CommonUtil;
import com.ehualu.eloc.sts.config.log.LogConfig;
import com.ehualu.eloc.sts.dao.DataBaseMapper;
import com.ehualu.eloc.sts.system.SystemUdpComponent;
import com.ehualu.eloc.sts.system.repository.CrossRepo;
import com.ehualu.eloc.sts.system.repository.SystemRepo;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Netty UDP Client
 *
 * @author Adam
 * @Date 2021/1/8 14:53
 * @Description
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdpBusPriorityClient {

    private Bootstrap bootstrap;
    private NioEventLoopGroup group;
    private ChannelFuture cf = null;

    //路口编号
    private String crossNo;
    //路口序号
    private String slcOrder;
    //信号机IP
    private String slcIp;
    private String host;
    //端口
    private Integer port;
    /**
     * 接收数据对象
     **/
    private ByteBuf receiveBuf;

    private SystemUdpComponent systemComponent = SpringContextUtils.getBean(SystemUdpComponent.class);
    private CrossRepo crossRepo = SpringContextUtils.getBean(CrossRepo.class);
    private SystemRepo systemRepo = SpringContextUtils.getBean(SystemRepo.class);
    private DataBaseMapper dataBaseMapper = SpringContextUtils.getBean(DataBaseMapper.class);
    private RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);
    private LogConfig logConfig= SpringContextUtils.getBean(LogConfig.class);
    /**
     * 获取接收数据
     *
     * @return
     */
    public ByteBuf getReceiveBuf() {
        synchronized (receiveBuf) {
            return receiveBuf;
        }
    }

    /**
     * 设置接收数据
     *
     * @param byteBuf
     */
    public void setReceiveBuf(ByteBuf byteBuf) {
        synchronized (receiveBuf) {
            receiveBuf = byteBuf;
        }
    }

    // Netty关闭
    public void shutDownClient() {
        group.shutdownGracefully();
        log.error("关闭了路口的netty：" + crossNo);
    }

    /**
     * 关闭通信
     */
    public void shutDownNetty() {
        this.group.shutdownGracefully();
    }

    public UdpBusPriorityClient(String crossNo, String slcIp, String slcOrder, Integer port) {
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        this.cf = null;
        this.crossNo = crossNo;
        this.slcIp = slcIp;
        this.host = slcIp;
        this.port = port;
        this.slcOrder = slcOrder;
        this.receiveBuf = Unpooled.buffer(1024);
//        this.init();
    }

    /*
     * @Author Adam
     * @Description 初始化
     * @Date 15:18 2021/1/8
     * @Param []
     * @return void
     */
    public void init() {
        try {
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UdpBusPriorityClientHandler(UdpBusPriorityClient.this));
            cf = bootstrap.bind(0).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送协议
     *
     * @Author: Adam
     * @Description
     * @Date: 2021/1/8 15:17
     * @Param: [msg]
     * @return: void
     */
    public void sendMessage(byte[] b) {
//        if("DEBUG".equals(logConfig.getLevel())){
//            log.info("发送： " + crossNo + "  " + CommonUtil.byte2String(b));
//        }
        log.info("发送： " + crossNo + "  " + CommonUtil.byte2String(b));
//        LogUtil.info(crossNo,"",b,1);
        try {
            cf.channel().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(b),
                    new InetSocketAddress(slcIp, port))).sync();
//                    new InetSocketAddress("192.168.110.100", 4000))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //发送 String 类型 协议
    public void sendMessage(String msg) {
        try {
            cf.channel().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                    new InetSocketAddress(slcIp, port))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
