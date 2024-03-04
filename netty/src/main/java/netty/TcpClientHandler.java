package netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;



/**
 * @version: V1.0
 * @author: szx
 * @className: TcpClientHandler
 * @description: TcpClient 助手类
 **/
@ChannelHandler.Sharable
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int i = 0;

    private TcpClient tcpClient;

    public TcpClientHandler(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }



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
            System.out.println( slcIp + ":上线！");
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
        ctx.deregister();
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
        e.printStackTrace();
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
