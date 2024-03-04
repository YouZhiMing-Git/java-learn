package netty.test.udp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author:youzhiming
 * @date: 2023/6/28
 * @description:
 */
public class UdpServer {
    EventLoopGroup group = new NioEventLoopGroup();

    int port;
    public UdpServer(int port){
        this.port=port;
    }

    public void start() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    // 主线程处理
                    .channel(NioDatagramChannel.class)
                    // 广播
                    .option(ChannelOption.SO_BROADCAST, true)
                    // 设置读缓冲区为2M
                    .option(ChannelOption.SO_RCVBUF, 2048 * 1024)
                    // 设置写缓冲区为1M
                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                    .handler(new UdpServerHandler());

            ChannelFuture f = bootstrap.bind(port).sync();
            System.out.println("Udp服务器正在监听......");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}
