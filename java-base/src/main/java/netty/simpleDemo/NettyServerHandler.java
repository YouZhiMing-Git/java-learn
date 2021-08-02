package netty.simpleDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/***
 *  自定义一个Handler，需要继承netty规定好的HandlerAdapter（规范）
 */

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /***
     * 读取数据实例
     *
     *
     * @param ctx 上下文对象，含有 管道pipeline 通道channel 地址
     * @param msg 客户端发送的数据，默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx ="+ctx);
        //将msg转为byteBuff(netty 提供)
        ByteBuf buf=(ByteBuf)msg;
        System.out.println("client send data is :" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("client address is " + ctx.channel().remoteAddress());

    }

    /***
     * 数据读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush=write+flush  写入并刷新
        //将数据写入缓存 并刷新
        //对发送的数据精选编发
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello client---",CharsetUtil.UTF_8));
    }

    /***
     * 处理异常，一般是关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().closeFuture();
    }
}
