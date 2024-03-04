package netty.test.client.netty;

import com.ehualu.eloc.common.dto.cross.CrossModeEnum;
import com.ehualu.eloc.common.frame.SpringContextUtils;
import com.ehualu.eloc.common.infrastructure.redis.RedisUtil;
import com.ehualu.eloc.common.protocol.recv.Pro0FC2;
import com.ehualu.eloc.common.protocol.send.*;
import com.ehualu.eloc.sts.system.SystemComponent;
import com.ehualu.eloc.sts.util.LogUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @version: V1.0
 * @author: szx
 * @className: TcpClient
 * @description: Socket 通信客户端类
 **/
@Slf4j
public class TcpClient {

    private SystemComponent systemComponent = SpringContextUtils.getBean(SystemComponent.class);

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

    private RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);

    private Integer equipmentId = 0;

    //信号机断开连接，正在重连标志
    private boolean isReConnecting;


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
        doConnect();
    }

    /*
     * @Author szx
     * @Description socket 执行方法
     * @Date 9:33 2020/12/25
     * @Param []
     * @return void
     **/
    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
//        log.info("{} 调用doConnect()", crossNo);
        ChannelFuture future;
//        try {
        //连接服务器 同步等待成功
//            future = bootstrap.connect(host, port).sync();
        future = bootstrap.connect(host, port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    log.info("连接成功！" + host + " " + port);
                    isReConnecting = false;
                    channel = futureListener.channel();
                    sendGetEquipmentId();
                } else {
                    isReConnecting = true;
                    //信号机状态改变
                    systemComponent.changeCrossState(crossNo, CrossModeEnum.CannotPing);
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            log.info("初始重连信号机：" + crossNo);
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }


    /*
     * @Author szx
     * @Description 发送消息方法
     * @Date 10:58 2020/12/25
     * @Param [b]
     * @return void
     **/
    public void sendMessage(byte[] b) {
        if (channel == null) return;
        if (!channel.isActive()) return;
        ByteBuf buffer = channel.alloc().buffer(b.length);
        buffer.writeBytes(b);
        channel.writeAndFlush(buffer);
        LogUtil.info(crossNo, "", b, 1);
    }

    /*
     * @Author Adam
     * @Description 发送消息方法,String
     * @Date 10:06 2021/12/9
     * @Param [msg]
     * @return void
     */
    public void sendMessageString(String msg) {
        if (channel == null) return;
        if (!channel.isActive()) return;
        channel.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
    }

    /*
     * @Author szx
     * @Description 发送初始化协议
     * @Date 9:39 2021/4/5
     * @Param []
     * @return void
     **/
    public void sendFirstProtocol() {
        log.info("发送初始协议：" + crossNo);
        // 1. 获取信号机编号
//        sendGetEquipmentId();
//        if (port != 4000) {
//            return;
//        }
        // 2. 获取信号机烧录版本
        sendGetSlcVersion();
        // 3.获取内部模块安装状态
        sendGetInnerModelState();
        // 4.获取信号机MAC地址
        sendGetSlcMacAddr();
        // 5.设置硬件初始化参数
        sendHwInitParam();
        // 6.系统对时
        sendSlcTime();
        // 7.路口当前方案+路口控制模式
        sendSlcState();
        // 8.设定检测器通道
        sendDetectChannelParameter();
    }

    /*
     * @Author Adam
     * @Description 发送认证登陆+初始化协议
     * @Date 16:29 2022/11/30
     * @Param [pro0FC2]
     * @return void
     */
    public void sendFirstProtocolAndLogIn(Pro0FC2 pro0FC2) {
        // 1. 登陆认证
        if (pro0FC2 != null) {
            sendLogIn(pro0FC2);
        }
        log.info("发送初始协议：" + crossNo);

        // 2. 获取信号机烧录版本
        sendGetSlcVersion();
        // 3.获取内部模块安装状态
        sendGetInnerModelState();
        // 4.获取信号机MAC地址
        sendGetSlcMacAddr();
        // 5.设置硬件初始化参数
        sendHwInitParam();
        // 6.系统对时
        sendSlcTime();
        // 7.路口当前方案+路口控制模式
        sendSlcState();
        // 8.设定检测器通道
        sendDetectChannelParameter();
        //9.初始获取信号机kernel类型 0-非核心板，1-核心板。
        sendKernelType();
    }

    /*
     * @Author Adam
     * @Description 登陆认证
     * @Date 16:28 2022/11/30
     * @Param [pro0FC2]
     * @return void
     */
    public boolean sendLogIn(Pro0FC2 pro0FC2) {
        log.info("登陆信号机!" + crossNo);
        if (pro0FC2 != null) {
            if (pro0FC2.getYear() == null || "".equals(pro0FC2.getYear())) {
                return false;
            }
            if (pro0FC2.getMonth() == null || "".equals(pro0FC2.getMonth())) {
                return false;
            }
            if (pro0FC2.getDay() == null || "".equals(pro0FC2.getDay())) {
                return false;
            }
            if (pro0FC2.getHour() == null || "".equals(pro0FC2.getHour())) {
                return false;
            }
            if (pro0FC2.getMin() == null || "".equals(pro0FC2.getMin())) {
                return false;
            }
            if (pro0FC2.getSec() == null || "".equals(pro0FC2.getSec())) {
                return false;
            }
            Pro5E24 pro5E24 = new Pro5E24(equipmentId);
            pro5E24.setCrossNo(crossNo);
            pro5E24.setConfigType("3");
            pro5E24.setUsername("admin");
            pro5E24.setPassword("ehl12345");
            pro5E24.setYear(pro0FC2.getYear());
            pro5E24.setMonth(pro0FC2.getMonth());
            pro5E24.setDay(pro0FC2.getDay());
            pro5E24.setHour(pro0FC2.getHour());
            pro5E24.setMin(pro0FC2.getMin());
            pro5E24.setSec(pro0FC2.getSec());

            //发送登陆认证协议
            sendMessage(pro5E24.fGetBytes());
        }
        return false;
    }

    /*
     * @Author szx
     * @Description 获取信号机编号
     * @Date 9:43 2021/4/5
     * @Param []
     * @return void
     **/
    public void sendGetEquipmentId() {
        log.info("获取equipmentId!" + crossNo);
        Pro0F40 pro0F40 = new Pro0F40();
        pro0F40.setCrossNo(crossNo);
        sendMessage(pro0F40.fGetBytes());
    }

    /*
     * @Author szx
     * @Description 获取信号机烧录版本
     * @Date 11:31 2021/4/5
     * @Param []
     * @return void
     **/
    public void sendGetSlcVersion() {
        Pro3066 pro3066 = new Pro3066(equipmentId);
        pro3066.setCrossNo(crossNo);
        sendMessage(pro3066.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 获取信号机当前硬件告警(信号机回复不准，故，放弃此协议)
     * @Date 9:15 2023/1/17
     * @Param []
     * @return void
     */
    public void sendGetSlcHardwareStatus() {
        Pro0F44 pro0F44 = new Pro0F44(equipmentId);
        pro0F44.setCrossNo(crossNo);
        sendMessage(pro0F44.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 获取内部模块安装状态
     * @Date 17:25 2021/5/26
     * @Param []
     * @return void
     */
    public void sendGetInnerModelState() {
        Pro3113 pro3113 = new Pro3113(equipmentId);
        pro3113.setCrossNo(crossNo);
        sendMessage(pro3113.fGetBytes());
    }

    /*
     * @Author szx
     * @Description 获取信号机MAC地址
     * @Date 9:20 2021/4/6
     * @Param []
     * @return void
     **/
    public void sendGetSlcMacAddr() {
        Pro30F5 pro30F5 = new Pro30F5(equipmentId);
        pro30F5.setCrossNo(crossNo);
        sendMessage(pro30F5.fGetBytes());
    }

    /*
     * @Author szx
     * @Description 设置硬件初始化参数
     * @Date 9:55 2021/4/6
     * @Param []
     * @return void
     **/
    public void sendHwInitParam() {
        Pro5F3F pro5F3F = new Pro5F3F(equipmentId);
        pro5F3F.setCrossNo(crossNo);
        pro5F3F.setTransmitCycle("0");
        sendMessage(pro5F3F.fGetBytes());
        Pro0F14 pro0F14 = new Pro0F14(equipmentId);
        pro0F14.setCrossNo(crossNo);
        pro0F14.setHardwareCycle("0");
        sendMessage(pro0F14.fGetBytes());
    }

    /*
     * @Author szx
     * @Description 系统对时
     * @Date 10:01 2021/4/6
     * @Param []
     * @return void
     **/
    public void sendSlcTime() {
        Pro0F12 pro0F12 = new Pro0F12(equipmentId);
        pro0F12.setCrossNo(crossNo);
        pro0F12.setNowTime();
        sendMessage(pro0F12.fGetBytes());
    }

    /*
     * @Author szx
     * @Description 路口当前方案+路口控制模式+控制策略
     * @Date 10:24 2021/4/9
     * @Param []
     * @return void
     **/
    public void sendSlcState() {
        Pro3042 pro3042 = new Pro3042(equipmentId);
        pro3042.setCrossNo(crossNo);
        sendMessage(pro3042.fGetBytes());
        Pro3040 pro3040 = new Pro3040(equipmentId);
        pro3040.setCrossNo(crossNo);
        sendMessage(pro3040.fGetBytes());
        Pro5F40 pro5F40 = new Pro5F40(equipmentId);
        pro5F40.setCrossNo(crossNo);
        sendMessage(pro5F40.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 获取当前相序方案
     * @Date 9:26 2022/1/7
     * @Param [phaseOrderNo]
     * @return void
     */
    public void sendSlcCurrentPhsOrdPlan(String phaseOrderNo) {
        Pro5F43 pro5F43 = new Pro5F43(equipmentId);
        pro5F43.setCrossNo(crossNo);
        pro5F43.setPhaseOrder(phaseOrderNo);
        sendMessage(pro5F43.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 路口控制模式+控制策略获取
     * @Date 9:26 2021/5/31
     * @Param []
     * @return void
     */
    public void sendCrossMode() {
        Pro3042 pro3042 = new Pro3042(equipmentId);
        pro3042.setCrossNo(crossNo);
        sendMessage(pro3042.fGetBytes());
        Pro5F40 pro5F40 = new Pro5F40(equipmentId);
        pro5F40.setCrossNo(crossNo);
        sendMessage(pro5F40.fGetBytes());
        if (equipmentId == 0) {
            System.out.println("equipmentId为：0," + crossNo);
        }

    }

    /*
     * @Author Adam
     * @Description 路口紧急控制模式获取
     * @Date 9:31 2021/10/13
     * @Param []
     * @return void
     */
    public void sendCrossEmergencyMode() {
        //设备编号
        Pro5F49 pro5F49 = new Pro5F49(equipmentId);
        pro5F49.setControlId("7");
        pro5F49.setActuateType("8");
        pro5F49.setCrossNo(crossNo);
        sendMessage(pro5F49.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 设定检测器通道
     * @Date 17:50 2021/7/28
     * @Param []
     * @return void
     */
    public void sendDetectChannelParameter() {
        //初始化是否重新下载检测器通道
        boolean initChanelFlag = redisUtil.getCrossDetectChannelInitFlag();
//        System.out.println("是否下载检测器通道：" + initChanelFlag);
        if (!initChanelFlag) return;
        Pro4030 crossPro4030 = redisUtil.getCrossPro4030(crossNo);
        if (crossPro4030 != null) {
            Pro4030 pro4030 = new Pro4030(equipmentId, crossPro4030.getMapPcodeOld());
            pro4030.setPhaseLan(crossPro4030.getPhaseLan());
            pro4030.setCrossNo(crossNo);
            sendMessage(pro4030.fGetBytes());
        }
    }

    /*
     * @Author w
     * @Description 针对批量升级，需要获取信号机核心板类型，0-非核心板-使用FTP升级方式，1-核心板-使用SFTP升级方式
     * @Date 2023/04/12
     * @Param []
     * @return void
     */
    public void sendKernelType() {
        Pro312A pro312A = new Pro312A(equipmentId);
        pro312A.setCrossNo(crossNo);
        sendMessage(pro312A.fGetBytes());
    }

    /*
     * @Author w
     * @Description 针对批量升级，核心板SFTP时，需开启SSH，
     * @Date 2023/04/24
     * @Param []
     * @return void
     */
    public void sendSetSsh(String sshSwitch) {
        Pro3126 pro3126 = new Pro3126(equipmentId);
        pro3126.setCrossNo(crossNo);
        pro3126.setSshSwitch(sshSwitch);
        sendMessage(pro3126.fGetBytes());
    }


    // 获取通道
    public Channel getChannel() {
        return channel;
    }

    // Netty关闭
    public void shutDownClient() {
//        group.shutdownGracefully();
//        channel.disconnect();
//        channel.eventLoop().shutdownGracefully();
//        channel.deregister();
//        ChannelFuture close = channel.close();
        group.shutdownGracefully();
        log.error("关闭了路口的netty：" + crossNo);
    }

    // 获取路口编号
    public String getCrossNo() {
        return crossNo;
    }

    // 设置路口编号
    public void setCrossNo(String crossNo) {
        this.crossNo = crossNo;
    }

    // 获取设备编号
    public Integer getEquipmentId() {
        return equipmentId;
    }

    // 设置设备编号
    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }


    // 信号机IP
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    // 信号机端口
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    // 信号机正在断开重连
    public boolean isReConnecting() {
        return isReConnecting;
    }

    public void setReConnecting(boolean reConnecting) {
        isReConnecting = reConnecting;
    }
}
