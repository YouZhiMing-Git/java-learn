package netty;





import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @version: V1.0
 * @author: szx
 * @className: TcpClient
 * @description: Socket 通信客户端类
 **/

public class TcpClient {


    /**
     * TcpClient 构造函数
     *
     * @param host 服务端 IP 地址
     * @param port 服务端端口
     */
    public TcpClient(int workerThread, String crossNo, String host, Integer port) {
//        this.group = group;
        bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup(2);
        this.crossNo = crossNo;
        this.host = host;
        this.port = port;
    }

    // 路口编号
    private String crossNo;
    // 服务端 IP 地址
    private String host;
    // 服务端端口
    private Integer port;
    // 客户端通道
    private Channel channel;
    // 启动对象
    private Bootstrap bootstrap;
    // Netty EventLoopGroup 对象
    private EventLoopGroup group;




    /*
     * @Author szx
     * @Description 初始化函数
     * @Date 9:32 2020/12/25
     * @Param []
     * @return void
     **/
    public void init() {
//        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)                        // 使用NioSocketChannel来作为连接用的channel类
                .handler(new ChannelInitializer<SocketChannel>() {      // 绑定连接初始化器
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline channelPipeline = ch.pipeline();
                        channelPipeline.addLast("ping",
                                new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        channelPipeline.addLast(new TcpClientDecoder());
                        channelPipeline.addLast(new TcpClientHandler(TcpClient.this));
                    }
                });
        ChannelFuture future;
        future = bootstrap.connect(host, port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {

                    channel = futureListener.channel();

                } else {

                }
            }
        });
    }


}
