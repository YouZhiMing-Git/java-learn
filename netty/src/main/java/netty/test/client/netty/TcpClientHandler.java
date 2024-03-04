package netty.test.client.netty;

import com.ehualu.eloc.common.dto.cross.CrossModeEnum;
import com.ehualu.eloc.common.frame.SpringContextUtils;
import com.ehualu.eloc.sts.system.SystemComponent;
import com.ehualu.eloc.sts.system.repository.SystemRepo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @version: V1.0
 * @author: szx
 * @className: TcpClientHandler
 * @description: TcpClient 助手类
 **/
@ChannelHandler.Sharable
@Slf4j
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private SystemComponent systemComponent = SpringContextUtils.getBean(SystemComponent.class);

    private int i = 0;

    private TcpClient tcpClient;

    public TcpClientHandler(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    private TcpClientProtocol clientProtocol = SpringContextUtils.getBean(TcpClientProtocol.class);

    private SystemRepo systemRepo = SpringContextUtils.getBean(SystemRepo.class);


    /**
     * Socket 接收消息函数，需要重写
     *
     * @param ctx
     * @param byteBuf 接收数据的对象
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        byte[] bTemp = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bTemp);
        clientProtocol.transferProtocol(tcpClient.getCrossNo(), bTemp);
    }

    /**
     * 新建连接时触发的函数
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
    }

    /**
     * 断开连接时触发的函数
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
    }

    /**
     * 在建立连接时触发的函数
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
            log.info(tcpClient.getCrossNo() + "," + slcIp + ":上线！");
        }
    }

    /**
     * 退出时触发的函数
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (systemComponent.getClientGroup().containsKey(tcpClient.getCrossNo())) {
            tcpClient.setReConnecting(true);
            log.info("修改reConnecting 为true");
            //信号机状态改变
            systemComponent.changeCrossState(tcpClient.getCrossNo(), CrossModeEnum.CannotPing);
            log.info("信号机断开连接：" + tcpClient.getCrossNo());
//            tcpClient.init();
            EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    tcpClient.doConnect();
                }
            }, 10, TimeUnit.SECONDS);
        } else {
            //信号机状态改变
            systemComponent.changeCrossState(tcpClient.getCrossNo(), CrossModeEnum.OffLine);
            ctx.pipeline().remove(this);
            ctx.deregister();
            log.info("注销信号机连接！" + tcpClient.getCrossNo());
        }
    }

    /**
     * 异常捕获
     *
     * @param ctx
     * @param e
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
//        log.error("信号机连接异常：" + tcpClient.getCrossNo());
//        System.out.println("信号机连接异常" + tcpClient.getCrossNo());
//        log.error("信号机连接异常" + tcpClient.getCrossNo());
        log.error(tcpClient.getCrossNo() + "异常", e.fillInStackTrace());
        ctx.close();
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
