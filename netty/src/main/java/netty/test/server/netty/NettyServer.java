package netty.test.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 服务端类
 *
 * @author Adam
 * @Date 2019/9/6
 */
@Slf4j
public class NettyServer {

    /**
     * boss 线程组用于处理连接工作
     */
    private EventLoopGroup boss = new NioEventLoopGroup();

    /**
     * work 线程组用于数据处理
     */
    private EventLoopGroup work = new NioEventLoopGroup();


    public void ServerStart() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                //1.指定 channel
                .channel(NioServerSocketChannel.class)
                //2.指定端口使用套接字
                .localAddress(4000)
                //3.服务端可连接队列数，对应TCP/IP协议 listener 函数中的backlog 参数
                .option(ChannelOption.SO_BACKLOG, 1024)
                //4.设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //5.将小的数据包包装成更大的帧进行传送，提高网络的负载
                .childOption(ChannelOption.TCP_NODELAY, true)
                //6.handler
                .childHandler(new NettyServerHandlerInitializer());

        try {
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                log.info("Start Netty Server Success!");
//                SysData.nettyServer = this;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("Start Netty Server Failed!");
        }

    }

//    @PreDestroy
//    public void ServerDestroy() throws InterruptedException {
//        boss.shutdownGracefully().sync();
//        work.shutdownGracefully().sync();
//        logger.info("关闭Netty");
//    }

}
