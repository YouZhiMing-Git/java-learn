package netty.discardServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author:youzhiming
 * @date: 2021/9/24
 * @description:
 */
public class DiscardServer {
    private int port;
    public DiscardServer(int port){
        this.port=port;
    }

    public void run() throws InterruptedException {
        /**
         * 用来处理io事件的多线程轮询
         * */
        EventLoopGroup bossGroup=new NioEventLoopGroup();//接受进来的连接
        EventLoopGroup workerGroup=new NioEventLoopGroup();//一旦boos接受连接，处理接收的流量，并将连接注册到worker


        try {
            ServerBootstrap b = new ServerBootstrap();//启动服务的帮助类
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//使用channel处理
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(port).sync();
            future.channel().closeFuture().sync();

        }finally {

        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new DiscardServer(port).run();
    }
}
