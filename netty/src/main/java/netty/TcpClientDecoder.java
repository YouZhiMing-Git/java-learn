package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version: 1.0
 * @Author: szx
 * @ClassName TcpClientDecoder
 * @Deacription 拆包粘包处理类
 **/

public class TcpClientDecoder extends ByteToMessageDecoder {

    // 用来临时保留没有处理过的请求报文
    ByteBuf tempMsg = Unpooled.buffer();

    // 解码器
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

    }

    

}
