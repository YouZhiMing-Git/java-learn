package netty.test.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Adam
 * @Date 2019/9/6
 */
public class NettyServerHandlerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        byte[] bytes = {(byte) 0x7D};
        ByteBuf delimiter = Unpooled.copiedBuffer(bytes);
        channel.pipeline()
                //定义空闲检测
//                .addLast(new ServerIdleStateHandler())
//                .addLast(new StringDecoder());
                //自定义 handler
                .addLast(
                        //拆包和粘包的解决方案,1-基于长度的协议
                        // 参数：
                        // 1.maxFrameLength：最大帧长度。也就是可以接收的数据的最大长度。如果超过，此次数据会被丢弃。
                        // 2.lengthFieldOffset：长度域偏移。就是说数据开始的几个字节可能不是表示数据长度，需要后移几个字节才是长度域。
                        // 3.lengthFieldLength：长度域字节数。用几个字节来表示数据长度。
                        //4.lengthAdjustment：数据长度修正。
                        //     --因为长度域指定的长度可以使 header+body 的整个长度，也可以只是body的长度。如果表示header+body的整个长度，那么我们需要修正数据长度。
                        //5.initialBytesToStrip：跳过的字节数。如果你需要接收 header+body 的所有数据，此值就是0，如果你只想接收body数据，那么需要跳过header所占用的字节数。
//                        new LengthFieldBasedFrameDecoder(65 * 1024, 1, 2),
//                        new DelimiterBasedFrameDecoder(65 * 1024, false, delimiter),
                        // 拆包和粘包的解决方案,2-基于特殊字符的协议
//                        new LineBasedFrameDecoder(65 * 1024),
                        //拆包和粘包的解决方案,3-重写基于特殊字符的协议
                        new MyFieldDecoder(65 * 1024, false, delimiter),
                        new NettyServerHandler()

                );

    }
}
