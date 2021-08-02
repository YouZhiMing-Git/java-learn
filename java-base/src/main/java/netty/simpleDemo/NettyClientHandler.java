package netty.simpleDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    /***
     * 当通道就绪就会触发该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client is : " + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello ,server ,喵喵喵", CharsetUtil.UTF_8));

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
        ByteBuf buf=(ByteBuf)msg;
        System.out.println("server response is : " + buf.toString(CharsetUtil.UTF_8));
        System.out.println("server address is " + ctx.channel().remoteAddress());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
