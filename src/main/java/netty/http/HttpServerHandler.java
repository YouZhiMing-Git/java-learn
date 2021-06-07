package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/***
 * HttpObject 客户端和服务端相互通讯的数据封装
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    //读取客户端
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof HttpRequest){
            System.out.println(msg.getClass());
            System.out.println(ctx.channel().remoteAddress());

            //回复信息给浏览器【Http 协议】
            ByteBuf buf = Unpooled.copiedBuffer("hello client,你好", CharsetUtil.UTF_8);

            //构造一个http响应
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,buf.readableBytes());
            ctx.writeAndFlush(response);
            System.out.println(response);
        }
    }
}
