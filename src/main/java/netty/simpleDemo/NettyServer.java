package netty.simpleDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {

        //创建BossGroup 和WorkGroup
        /*
         * 1. 创建两个线程组 bossGroup 和workerGroup
         * 2. bossGroup只是处理连接请求 真正和客户端业务处理 会交给workGroup
         * 3. 两个都是无限循环
         * 4. bossGroup和workGroup含有子线程（NioEventLoop）的个数 默认实际cpu核数*2
         * */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //创建爱你服务器启动对象，配置参数
            ServerBootstrap b = new ServerBootstrap();

            //使用链式编程进行设置
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)//s
                    .option(ChannelOption.SO_BACKLOG, 128)//设置线程队列最大连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });//给workerGroup 的EventLoop设置处理器
            System.out.println("--------server is ready-------------");
            //绑定一个端口并且同步，生成一个ChannelFuture对象
            //启动服务器
            ChannelFuture cf = b.bind(6688).sync();

            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
