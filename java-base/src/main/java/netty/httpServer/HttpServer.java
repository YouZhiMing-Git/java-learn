package netty.httpServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;




public class HttpServer {
    private  int port;

    public  HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new HttpServer(8800).start();
    }
    public void start() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        System.out.println("initChannel socketChannel:" + socketChannel);
                        socketChannel.pipeline()
                                .addLast("decoder",new HttpRequestDecoder())//用于解码request
                                .addLast("encoder",new HttpRequestEncoder())//用于编码repose
//                                .addLast("aggregator",new HttpObjectAggregator(512*1024))//消息合并的数据大小，如此代表聚合的消息内容长度不超过512kb。
                                .addLast("handler",new HttpHandler());//添加我们自己的处理接口

                    }
                })
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE);
        b.bind(port).sync();

    }

}
