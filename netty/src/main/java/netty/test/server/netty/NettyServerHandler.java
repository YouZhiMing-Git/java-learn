package netty.test.server.netty;

import com.ehualu.eloc.common.dto.cross.CrossModeEnum;
import com.ehualu.eloc.common.frame.SpringContextUtils;
import com.ehualu.eloc.sts.system.SystemComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * Netty Server Handler
 *
 * @author Adam
 * @Date 2019/9/6
 */
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private NettyServerProtocol nettyServerProtocol = SpringContextUtils.getBean(NettyServerProtocol.class);
    private SystemComponent systemComponent = SpringContextUtils.getBean(SystemComponent.class);

    /**
     * 接收数据
     *
     * @param ctx
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        Channel channel = ctx.channel();
        //信号机IP
        String slcIp = getIp(channel.remoteAddress().toString());
        //处理收到的协议
        nettyServerProtocol.transferProtocol(slcIp, byteBuf);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("添加了：" + ctx.channel().remoteAddress());
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.error("移除了：" + ctx.channel().remoteAddress());
        super.handlerRemoved(ctx);
    }

    /**
     * 连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        boolean active = channel.isActive();
        String ipAddress = channel.remoteAddress().toString();
        String slcIp = getIp(ipAddress);
        if (active) {
            //上线的信号机 IP-Channel 放入缓存
            if (systemComponent.getChannelGroup().containsKey(slcIp)) {
                NettyServerSrc nettyServerSrc = systemComponent.getChannelGroup().get(slcIp);
                if (nettyServerSrc == null) {
                    nettyServerSrc = new NettyServerSrc();
                    nettyServerSrc.setCrossNoList(new ArrayList<>());
                    nettyServerSrc.setSlcIp(slcIp);
                    nettyServerSrc.setChannel(channel);
                } else {
                    Channel preChannel= systemComponent.getChannelGroup().get(slcIp).getChannel();
                    if(preChannel != null&&preChannel.isActive()&& preChannel.isOpen()){
                        preChannel.close();
                    }
                    systemComponent.getChannelGroup().get(slcIp).setChannel(channel);
                }
            } else {
                systemComponent.getChannelGroup().put(slcIp, NettyServerSrc.builder()
                        .slcIp(slcIp)
                        .channel(channel)
                        .crossNoList(new ArrayList<>())
                        .build());
            }

            log.info("[" + getIp(ipAddress) + "] is online");

            //如果路口在缓存，并且路口不是脱机状态，路口上线后发送初始协议
            List<String> crossNoList = systemComponent.getChannelGroup().get(slcIp).getCrossNoList();
            if (crossNoList != null && crossNoList.size() > 0) {
                try {
                    //上线的信号机IP发送初始协议
                    systemComponent.sendInitPro(slcIp);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } else {
            log.error("[" + getIp(ipAddress) + "] is offline");
        }
    }

    /**
     * 连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error(ctx.channel() + "断开！");
        Channel channel = ctx.channel();
        String ipAddress = channel.remoteAddress().toString();
        String slcIp = getIp(ipAddress);
        //设置路口脱机
        changeSlcOffLine(slcIp);
        super.channelInactive(ctx);
    }

    /**
     * 出现异常的时候
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("断开异常：" + cause);
        Channel channel = ctx.channel();
        log.error("[" + channel.remoteAddress() + "] leave the room");
        String ipAddress = channel.remoteAddress().toString();
        String slcIp = getIp(ipAddress);
      /*  //设置路口脱机
        changeSlcOffLine(slcIp);
        ctx.close().sync();*/
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

    /**
     * @Author: Adam
     * @Description 设置信号机IP脱机
     * @Date: 2021/6/25 14:10
     * @Param: [slcIp]
     * @return: void
     */
    private void changeSlcOffLine(String slcIp) {
        if (systemComponent.getChannelGroup().containsKey(slcIp)) {
            NettyServerSrc nettyServerSrc = systemComponent.getChannelGroup().get(slcIp);
            if (nettyServerSrc != null) {
                List<String> crossNoList = nettyServerSrc.getCrossNoList();
                if (crossNoList != null && crossNoList.size() > 0) {
                    for (int i = 0; i < crossNoList.size(); i++) {
                        String crossNo = crossNoList.get(i);
                        systemComponent.changeCrossState(crossNo, CrossModeEnum.OffLine);
                    }
                }
            }
        }
    }
}
