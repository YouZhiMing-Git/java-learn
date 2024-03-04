package netty.test.udp.netty;

import com.alibaba.fastjson.JSON;
import com.ehualu.eloc.common.entity.mysql.*;
import com.ehualu.eloc.common.frame.SpringContextUtils;
import com.ehualu.eloc.common.infrastructure.redis.RedisUtil;
import com.ehualu.eloc.common.protocolhaixin.recv.Pro4R22;
import com.ehualu.eloc.common.protocolhaixin.send.*;
import com.ehualu.eloc.common.util.CommonUtil;
import com.ehualu.eloc.common.util.TimeUtil;
import com.ehualu.eloc.sts.config.log.LogConfig;
import com.ehualu.eloc.sts.dao.DataBaseMapper;
import com.ehualu.eloc.sts.service.CrossService;
import com.ehualu.eloc.sts.system.SystemUdpComponent;
import com.ehualu.eloc.sts.system.repository.CrossRepo;
import com.ehualu.eloc.sts.system.repository.SystemRepo;
import com.ehualu.eloc.sts.util.LogUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Netty UDP Client
 *
 * @author Adam
 * @Date 2021/1/8 14:53
 * @Description
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdpClient {

    private Bootstrap bootstrap;
    private NioEventLoopGroup group;
    private ChannelFuture cf = null;

    //路口编号
    private String crossNo;
    //路口序号
    private String slcOrder;
    //信号机IP
    private String slcIp;
    private String host;
    //端口
    private Integer port;
    //协议接收最新时间
    private int receiveTime;


    private volatile boolean hasConnect = false;
    private volatile int initFlag = 0;

    private int connectCount = 0;
    /**
     * 接收数据对象
     **/
    private ByteBuf receiveBuf;

    private SystemUdpComponent systemComponent = SpringContextUtils.getBean(SystemUdpComponent.class);
    private CrossRepo crossRepo = SpringContextUtils.getBean(CrossRepo.class);
    private SystemRepo systemRepo = SpringContextUtils.getBean(SystemRepo.class);
    private DataBaseMapper dataBaseMapper = SpringContextUtils.getBean(DataBaseMapper.class);
    private RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);
    private LogConfig logConfig = SpringContextUtils.getBean(LogConfig.class);
    private CrossService crossService = SpringContextUtils.getBean(CrossService.class);

    /**
     * 获取接收数据
     *
     * @return
     */
    public ByteBuf getReceiveBuf() {
        synchronized (receiveBuf) {
            return receiveBuf;
        }
    }

    /**
     * 设置接收数据
     *
     * @param byteBuf
     */
    public void setReceiveBuf(ByteBuf byteBuf) {
        synchronized (receiveBuf) {
            receiveBuf = byteBuf;
        }
    }

    // Netty关闭
    public void shutDownClient() {
        group.shutdownGracefully();
        log.error("关闭了路口的netty：" + crossNo);
    }

    /**
     * 关闭通信
     */
    public void shutDownNetty() {
        this.group.shutdownGracefully();
    }

    public UdpClient(String crossNo, String slcIp, String slcOrder, Integer port) {
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        this.cf = null;
        this.crossNo = crossNo;
        this.slcIp = slcIp;
        this.host = slcIp;
        this.port = port;
        this.slcOrder = slcOrder;
        this.receiveBuf = Unpooled.buffer(1024);
//        this.init();
    }

    /*
     * @Author Adam
     * @Description 初始化
     * @Date 15:18 2021/1/8
     * @Param []
     * @return void
     */
    public void init() {
        hasConnect = false;
        connectCount++;
        try {
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UdpClientHandler(UdpClient.this));
            cf = bootstrap.bind(0).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (cf != null) {
            if (tryConnect()) {
                hasConnect = true;
                log.info(crossNo + "：udp连接成功！");
                sendInitPro();
            }

        }
        initFlag = 1;

    }

    /**
     * 发送协议
     *
     * @Author: Adam
     * @Description
     * @Date: 2021/1/8 15:17
     * @Param: [msg]
     * @return: void
     */
    public void sendMessage(byte[] b) {
        if (!CommonUtil.byte2String(b).equals("7E,00,0C,10,00,01,00,00,00,01,01,00,70,FA,92,7D")) {
//            log.info("发送： " + crossNo + "  " + CommonUtil.byte2String(b));
            LogUtil.info(crossNo, "", b, 1);
        }
        try {
            cf.channel().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(b),
                    new InetSocketAddress(slcIp, port))).sync();
//                    new InetSocketAddress("192.168.110.100", 4000))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //发送 String 类型 协议
    public void sendMessage(String msg) {
        try {
            cf.channel().writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8),
                    new InetSocketAddress(slcIp, port))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /*
     * @Author Adam
     * @Description 发送初始协议
     * @Date 15:15 2021/6/17
     * @Param [slcIp]
     * @return void
     */
    public void sendInitPro() {
        //获取信号机 IP 对应的 crossNo
        if (crossNo == null) return;

        log.info("发送初始协议！");

        /** 获取信号机版本*/
        sendGetSlcVersion();
        /** 校时信号机 **/
        sendSetSlcTime();
        /** 若未配置相位灯组关系，则发送默认关系 **/
        sendInitPhaseLightNo();
        /** 获取相位灯组信息*/
        sendGetSlcPhaseLight();
        /** 获取控制策略 **/
        sendGetControlState();
        /** 获取当前配时方案 **/
        sendGetCurrPlan();

        /** 获取当前手动状态*/
        sendGetManualType();
        clearDetectChannel();



        /** 设定检测通道组态定义 **/
        sendSetDetectChannel();

        sendOverControlInfo();

        systemRepo.getScheduledExecutorService().schedule(() -> {
            //初始化相位灯组
            systemComponent.initCrossPhaseLight(crossNo);
            //初始化接线端子类型
            systemComponent.initCrossLanePort(crossNo);
            sendSetDetectChannel();
        }, 3, TimeUnit.SECONDS);


        clearInfo();
        sendLaneInfo();
//        getLaneInfo();
    }


    public boolean tryConnect() {
        Pro1S20 pro1S20 = new Pro1S20(slcIp);
        pro1S20.setCrossNo(crossNo);
        crossRepo.putQueue(crossNo, pro1S20.getReProtocol(), "", "");
        sendMessage(pro1S20.fGetBytes());
        Object object = crossRepo.getQueue(crossNo, pro1S20.getReProtocol(), "", "");
        log.info("路口{}测试连接结果为：{}", crossNo, (object != null));
        return object != null;
    }

    /*
     * @Author Adam
     * @Description 获取当前方案
     * @Date 10:37 2021/6/25
     * @Param [slcIp, crossNo, slcOrder]
     * @return void
     */
    public void sendGetCurrPlan() {
        //查询当前方案
        Pro13S23 pro13S23 = new Pro13S23(slcIp);
        pro13S23.setCrossNo(crossNo);
        pro13S23.setCrossIdNo(slcOrder);
        sendMessage(pro13S23.fGetBytes());
    }

    public void sendGetManualType() {

        Pro130S20 pro130D20 = new Pro130S20(slcIp);
        pro130D20.setCrossNo(crossNo);
        sendMessage(pro130D20.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 获取路口资源
     * @Date 18:04 2021/7/6
     * @Param [slcIp, crossNo, slcOrder]
     * @return void
     */
    private void getSlcCrossSource(String slcIp, String crossNo, String slcOrder) {
        //路口资源
        Pro137S10 pro137S10 = new Pro137S10(slcIp);
        pro137S10.setCrossNo(crossNo);
        pro137S10.setCrossIdNo(slcOrder);
        sendMessage(pro137S10.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 对时信号机
     * @Date 18:09 2021/7/6
     * @Param [slcIp]
     * @return void
     */
    public void sendSetSlcTime() {
        Pro13D111 pro13D111 = new Pro13D111(slcIp);
        pro13D111.setCrossNo(crossNo);
        String year = TimeUtil.getYear();
        String month = TimeUtil.getMonth();
        String day = TimeUtil.getDay();
        String hour = TimeUtil.getHour();
        String minute = TimeUtil.getMinute();
        String second = TimeUtil.getSecond();
        pro13D111.setYear(year);
        pro13D111.setMonth(month);
        pro13D111.setDay(day);
        pro13D111.setHour(hour);
        pro13D111.setMinute(minute);
        pro13D111.setSecond(second);
        sendMessage(pro13D111.fGetBytes());
    }

    /*
     * @Author Adam
     * @Description 获取控制策略
     * @Date 18:24 2021/7/6
     * @Param [slcIp, crossNo, slcOrder]
     * @return void
     */
    private void sendGetControlState() {
        //1.控制策略
        Pro140S30 pro140S30 = new Pro140S30(slcIp);
        pro140S30.setCrossNo(crossNo);
        pro140S30.setCrossIdNo(slcOrder);
        sendMessage(pro140S30.fGetBytes());
        //2.获取控制模式
        Pro13S20 pro13S20 = new Pro13S20(slcIp);
        pro13S20.setCrossNo(crossNo);
        pro13S20.setCrossIdNo(slcOrder);
        sendMessage(pro13S20.fGetBytes());
    }


    /**
     * 获取信号机版本
     */
    private void sendGetSlcVersion() {
        Pro1S20 pro1S20 = new Pro1S20(slcIp);
        pro1S20.setCrossNo(crossNo);
        sendMessage(pro1S20.fGetBytes());
    }

    /**
     * 获取信号机相位与灯号关系
     */
    public void sendGetSlcPhaseLight() {
        List<String> phaseNoList = new ArrayList<>();
        for (int i = 1; i < 33; i++) {
            phaseNoList.add(i + "");
        }
        Pro4S22 pro4S22 = new Pro4S22(slcIp);
        pro4S22.setCrossNo(crossNo);
        pro4S22.setPhaseNoList(phaseNoList);
        sendMessage(pro4S22.fGetBytes());
    }

    @Deprecated
    private void sendSetDetectChannel1() {
        List<EtCrossDectKind> crossDetectKind = crossService.getEtCrossDetectKind(Integer.parseInt(crossNo));
        if (crossDetectKind == null || crossDetectKind.size() == 0) return;

        List<List<Integer>> queDetectList = new ArrayList<>();
        List<List<Integer>> overDetectList = new ArrayList<>();
        List<List<Integer>> inductionDetectList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            queDetectList.add(i, list);
            overDetectList.add(i, list);
            inductionDetectList.add(i, list);
        }
        //1.下载相位检测器通道对应关系
        if (crossDetectKind != null && crossDetectKind.size() > 0) {

            //各类检测器通道参数集合
            //排队
            Map<Integer, List<Integer>> queDetectMap = new HashMap<>();
            //反溢
            Map<Integer, List<Integer>> overDetectMap = new HashMap<>();
            //感应（一般检测器、紧急检测器）
            Map<Integer, List<Integer>> inductionDetectMap = new HashMap<>();

            for (int i = 0; i < crossDetectKind.size(); i++) {
                EtCrossDectKind item = crossDetectKind.get(i);
                //检测器类型
                int detKind = Integer.parseInt(item.getDetKind());
                if (detKind != 0) {
                    //相位编号
                    int signalPort = item.getSignalPort();
                    //通道编号
                    int channelNo = Integer.parseInt(item.getChannelNo());
                    switch (detKind) {
                        case 3:
                            //排队
                            //排队检测器编号
                            queDetectMap = setDetectChannelNo(queDetectMap, signalPort, channelNo);
                            break;
                        case 2:
                            //反溢
                            overDetectMap = setDetectChannelNo(overDetectMap, signalPort, channelNo);
                            break;
                        case 6:
                            //感应（一般检测器）
                        case 4:
                            //紧急
                            inductionDetectMap = setDetectChannelNo(inductionDetectMap, signalPort, channelNo);
                            break;
                        default:
                            break;
                    }
                }
            }

            if (queDetectMap != null && queDetectMap.size() > 0) {
                for (int signalPort : queDetectMap.keySet()) {
                    List<Integer> listChannelNo = queDetectMap.get(signalPort);
                    queDetectList.set(signalPort - 1, listChannelNo);
                }

            }
            if (overDetectMap != null && overDetectMap.size() > 0) {
                for (int signalPort : overDetectMap.keySet()) {
                    List<Integer> listChannelNo = overDetectMap.get(signalPort);
                    overDetectList.set(signalPort - 1, listChannelNo);
                }
            }
            if (inductionDetectMap != null && inductionDetectMap.size() > 0) {
                for (int signalPort : inductionDetectMap.keySet()) {
                    List<Integer> listChannelNo = inductionDetectMap.get(signalPort);
                    inductionDetectList.set(signalPort - 1, listChannelNo);
                }
            }

        }
        //排队
        Pro146D10 pro1 = new Pro146D10(slcIp);
        pro1.setCrossNo(crossNo);
//        pro1.setQueueDetectNoList(queDetectList);
        sendMessage(pro1.fGetBytes());


        //反溢
        Pro146D10 pro2 = new Pro146D10(slcIp);
        pro2.setCrossNo(crossNo);
//        pro2.setReverseDetectNoList(overDetectList);
        sendMessage(pro2.fGetBytes());

        //感应
        Pro4D20 pro3 = new Pro4D20(slcIp);
        pro3.setCrossNo(crossNo);
        pro3.setDetectChannelFlag("1");
        pro3.setInductionDetectList(inductionDetectList);
        sendMessage(pro3.fGetBytes());
    }



    public void clearDetectChannel() {
        Pro129D10 pro129D10 = new Pro129D10(slcIp);
        pro129D10.setCrossNo(crossNo);
        pro129D10.setOrder("5");
        sendMessage(pro129D10.fGetBytes());

        //清空检测器信息
        for (int i = 0; i < 32; i++) {
            Pro5D20 pro5D20 = new Pro5D20(slcIp);
            pro5D20.setCrossNo(crossNo);
            pro5D20.setDetectNo(i + 1 + "");
            pro5D20.setOccupancyGatherCycle("0");
            pro5D20.setVolumeGatherCycle("0");
            pro5D20.setType("0");
            sendMessage(pro5D20.fGetBytes());
        }

        for (int i = 0; i < 32; i++) {
            Pro133D20 pro133D20 = new Pro133D20(slcIp);
            pro133D20.setCrossNo(crossNo);
            pro133D20.setParamNo(i + 1 + "");
            pro133D20.setDetectOption(new ArrayList<>());
            pro133D20.setCongestionTriggerOccupancy("0");
            pro133D20.setOverflowSecureOccupancy("0");
            sendMessage(pro133D20.fGetBytes());

        }


        //清空相位配置信息
        List<List<Integer>> detectList = new ArrayList<>();
        List<String> extendGreenList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            detectList.add(i, list);
            extendGreenList.add("0");
        }
        Pro4D20 pro3 = new Pro4D20(slcIp);
        pro3.setCrossNo(crossNo);
        pro3.setDetectChannelFlag("1");
        pro3.setExtendGreenFlag("1");
        pro3.setInductionDetectList(detectList);
        pro3.setExtendGreenList(extendGreenList);
        sendMessage(pro3.fGetBytes());


        pro129D10.setOrder("1");
        sendMessage(pro129D10.fGetBytes());
        pro129D10.setOrder("6");
        sendMessage(pro129D10.fGetBytes());


    }

    public void sendSetDetectChannel() {
        //灯组对应相位
        Map<String, List<String>> lightPhaseMap = redisUtil.getCrossLightPhase(crossNo);
        List<EtCrossDectKind> crossDetectKind = crossService.getEtCrossDetectKind(Integer.parseInt(crossNo));
        List<EtCrossDetect> etCrossDetectList = crossService.getEtCrossDetectByCrossNo(crossNo);
        List<EtPedestrianNonMotorized> etPedestrianNonMotorizedList = crossService.getPedDetectKind(crossNo);

        if(lightPhaseMap==null)return;

        List<List<Integer>> detectList = new ArrayList<>();
        List<String> extendGreenList = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            List<Integer> list = new ArrayList<>();
            list.add(0);
            detectList.add(i, list);
            extendGreenList.add("0");
        }
        //1.下载相位检测器通道对应关系
        if (crossDetectKind != null && crossDetectKind.size() > 0) {

            //各类检测器通道参数集合
            Map<Integer, List<Integer>> detectMap = new HashMap<>();
            for (int i = 0; i < crossDetectKind.size(); i++) {
                EtCrossDectKind item = crossDetectKind.get(i);
                //检测器类型
                int detKind = Integer.parseInt(item.getDetKind());
                if (detKind != 0) {
                    int signalPort = item.getSignalPort();
                    List<String> list = lightPhaseMap.get(signalPort + "");
                    if (list!=null&&list.size()>0){
                        String phaseNo = list.get(0);
                        int phaseNoInt = Integer.parseInt(phaseNo);
                        //通道编号
                        int channelNo = Integer.parseInt(item.getChannelNo());
                        if (!detectMap.containsKey(phaseNoInt)) {

                            List<Integer> temp = new ArrayList<>();
                            temp.add(channelNo);
                            detectMap.put(phaseNoInt, temp);
                        } else {
                            List<Integer> temp = detectMap.get(phaseNoInt);
                            if (!temp.contains(channelNo)) {
                                temp.add(channelNo);
                            }

                        }
                    }

//                    detectMap = setDetectChannelNo(detectMap, signalPort, channelNo);
                }
                if (detKind == 6) {
                    int signalPort = item.getSignalPort();

                    List<String> list = lightPhaseMap.get(signalPort + "");
                    if (list!=null&&list.size()>0){
                        String phaseNo = list.get(0);
                        int phaseNoInt = Integer.parseInt(phaseNo);

                        //相位延长绿 设置为3s
                        extendGreenList.set(phaseNoInt - 1, "30");
                    }


                    extendGreenList.set(signalPort-1,"30");

                    extendGreenList.set(signalPort - 1, "30");

                }

            }
            if (detectMap != null && detectMap.size() > 0) {
                for (int phaseNo : detectMap.keySet()) {

                    List<Integer> listChannelNo = detectMap.get(phaseNo);
                    detectList.set(phaseNo - 1, listChannelNo);
                }

            }

        }

        List<Pro5D20> pro5D20List = new ArrayList<>();
        List<Pro133D20> pro133D20List = new ArrayList<>();
        if (etCrossDetectList != null && etCrossDetectList.size() > 0) {
            for (EtCrossDetect dect : etCrossDetectList) {
                if (!StringUtils.isEmpty(dect.getLaneNo())) {
                    Pro5D20 pro5D20 = new Pro5D20(slcIp);
                    pro5D20.setDetectNo(dect.getChannelId());
                    pro5D20.setOccupancyGatherCycle("300");
                    pro5D20.setVolumeGatherCycle("300");
//                    pro5D20.setType(convertDetectType(dect.getSimulatorType()));
                    pro5D20.setType("11");//网口
                    pro5D20List.add(pro5D20);


                    List<String> optionList = new ArrayList<>();
                    if (!"0".equals(convertChannelType(dect.getChannelType()))) {
                        optionList.add(convertChannelType(dect.getChannelType()));
                    }
                    Pro133D20 pro133D20 = new Pro133D20(slcIp);
                    pro133D20.setCrossNo(crossNo);
                    pro133D20.setParamNo(dect.getChannelId());
                    pro133D20.setDetectOption(optionList);
                    if ("2".equals(dect.getChannelType()) ||
                            "3".equals(dect.getChannelType())) {
                        pro133D20.setCongestionTriggerOccupancy("60");
                        pro133D20.setOverflowSecureOccupancy("40");
                    } else {
                        pro133D20.setCongestionTriggerOccupancy("0");
                        pro133D20.setOverflowSecureOccupancy("0");
                    }
                    pro133D20List.add(pro133D20);
                }

            }
        }

        if (etPedestrianNonMotorizedList != null && etPedestrianNonMotorizedList.size() > 0) {

            Map<Integer, List<Integer>> pedDetectMap = new HashMap<>();
            for (EtPedestrianNonMotorized pedDetect : etPedestrianNonMotorizedList) {
                String channelId = pedDetect.getChannelId();
                String phaseNo = pedDetect.getLaneKind();
                int phaseNoInt = Integer.parseInt(phaseNo);
                int channelIdInt = Integer.parseInt(channelId);
                if (pedDetectMap.containsKey(phaseNoInt)) {
                    pedDetectMap.get(phaseNoInt).add(channelIdInt);
                } else {
                    List<Integer> temp = new ArrayList<>();
                    temp.add(channelIdInt);
                    pedDetectMap.put(phaseNoInt, temp);
                }

            }
            for (Integer signalPort : pedDetectMap.keySet()) {
                List<Integer> listChannelNo = pedDetectMap.get(signalPort);
                detectList.set(signalPort - 1, listChannelNo);
            }


            for (EtPedestrianNonMotorized pedDetect : etPedestrianNonMotorizedList) {

                Pro5D20 pro5D20 = new Pro5D20(slcIp);
                pro5D20.setDetectNo(pedDetect.getChannelId());
                pro5D20.setOccupancyGatherCycle("300");
                pro5D20.setVolumeGatherCycle("300");
                pro5D20.setType("1");
                pro5D20List.add(pro5D20);


                List<String> optionList = new ArrayList<>();
                optionList.add("1");
                Pro133D20 pro133D20 = new Pro133D20(slcIp);
                pro133D20.setCrossNo(crossNo);
                pro133D20.setParamNo(pedDetect.getChannelId());
                pro133D20.setDetectOption(optionList);
                pro133D20.setCongestionTriggerOccupancy("0");
                pro133D20.setOverflowSecureOccupancy("0");
                pro133D20List.add(pro133D20);

            }
        }


        log.info("cross {} detectInfo size {}", crossNo, pro5D20List.size());

        Pro129D10 pro129D10 = new Pro129D10(slcIp);
        pro129D10.setCrossNo(crossNo);
        pro129D10.setOrder("5");
        sendMessage(pro129D10.fGetBytes());



        log.info("【crossNo {}】====pro5D20List {} \npro133D20List {}",crossNo,JSON.toJSONString(pro5D20List),JSON.toJSONString(pro133D20List));
        for (Pro5D20 pro5D20 : pro5D20List) {
            sendMessage(pro5D20.fGetBytes());
        }
        for (Pro133D20 pro133D20 : pro133D20List) {
            sendMessage(pro133D20.fGetBytes());
        }


        Pro4D20 pro3 = new Pro4D20(slcIp);
        pro3.setCrossNo(crossNo);
        pro3.setDetectChannelFlag("1");
        pro3.setInductionDetectList(detectList);
        sendMessage(pro3.fGetBytes());

        Pro4D20 pro4 = new Pro4D20(slcIp);
        pro4.setCrossNo(crossNo);
        pro4.setExtendGreenFlag("1");
        pro4.setExtendGreenList(extendGreenList);
        log.info("【crossNo {}】====pro4 {}", crossNo, JSON.toJSONString(pro4));

        sendMessage(pro4.fGetBytes());


        pro129D10.setOrder("1");
        sendMessage(pro129D10.fGetBytes());
        pro129D10.setOrder("6");
        sendMessage(pro129D10.fGetBytes());
    }


    public void clearInfo() {


        Pro129D10 pro129D10 = new Pro129D10(slcIp);
        pro129D10.setCrossNo(crossNo);
        pro129D10.setOrder("5");
        sendMessage(pro129D10.fGetBytes());
        for (int i = 0; i < 16; i++) {
            Pro137D20 pro = new Pro137D20(slcIp);
            pro.setCrossNo(crossNo);
            pro.setLaneNo(i + 1 + "");
            pro.setDir("-1");
            pro.setLightNo("0");
            pro.setLocation("0");
            pro.setType("0");
            pro.setSeqNo("0");
            pro.setLaneFlow(new ArrayList<>());
            pro.setExistAgency("0");
            pro.setAgencyLightNo("0");
            sendMessage(pro.fGetBytes());

            Pro138D10 pro138D10 = new Pro138D10(slcIp);
            pro138D10.setCrossNo(crossNo);
            pro138D10.setType("0");
            pro138D10.setLightNo(i + 1 + "");
            pro138D10.setFlows(new ArrayList<>());
            pro138D10.setDir("-1");
            sendMessage(pro138D10.fGetBytes());


            Pro131D45 pro131D45 = new Pro131D45(slcIp);
            pro131D45.setCrossNo(crossNo);
            pro131D45.setLightNo(i + 1 + "");
            pro131D45.setIndex("0");

            sendMessage(pro131D45.fGetBytes());
        }
        pro129D10.setOrder("1");
        sendMessage(pro129D10.fGetBytes());
        pro129D10.setOrder("6");
        sendMessage(pro129D10.fGetBytes());
    }

    /**
     * 发送车道及灯组信息
     */
    public void sendLaneInfo() {
        List<EtCrossLane> crossLanes = dataBaseMapper.getCrossLane(crossNo);
        Map<Integer, String> crossStructMap = redisUtil.getCrossStructMap(crossNo);


        Pro129D10 pro129D10 = new Pro129D10(slcIp);
        pro129D10.setCrossNo(crossNo);
        pro129D10.setOrder("5");
        sendMessage(pro129D10.fGetBytes());


        int index = 1;
        for (int i = 0; i < crossLanes.size(); i++) {
            String laneNo = i + 1 + "";
            EtCrossLane etCrossLane = crossLanes.get(i);
            String type = etCrossLane.getType();
            String flow = etCrossLane.getFlow();
            String lightNo = etCrossLane.getLightNo();
            String dir = etCrossLane.getDir();
            String seqNo = etCrossLane.getSeqNo();
            List<String> flows = getFlows(flow);

            Pro137D20 pro137D20 = new Pro137D20(slcIp);
            pro137D20.setCrossNo(crossNo);
            pro137D20.setLaneNo(laneNo);
            pro137D20.setDir(dir);
            pro137D20.setLightNo(lightNo);
            pro137D20.setLocation("1");
            pro137D20.setType(covertType(type));
            //机动车
            if (Integer.parseInt(flow) < 100) {
                pro137D20.setSeqNo(seqNo);
                pro137D20.setLaneFlow(flows);

            } else {
                //入口1  出入口2 出口3，同灯组那边的行人
                String pedSeqNo = "2";
                if ("100".equals(seqNo)) {
                    pedSeqNo = "2";
                } else if ("101".equals(seqNo)) {
                    pedSeqNo = "1";
                } else if ("102".equals(seqNo)) {
                    pedSeqNo = "3";
                }
                pro137D20.setSeqNo(pedSeqNo);
                pro137D20.setLaneFlow(new ArrayList<String>());
            }
            //判断当前是否含有待转
            if (flows.contains("0") && crossStructMap != null && "1".equals(crossStructMap.get(Integer.parseInt(dir)))) {
                pro137D20.setExistAgency("1");
                pro137D20.setAgencyLightNo(lightNo);
            } else {
                pro137D20.setExistAgency("0");
                pro137D20.setAgencyLightNo("0");
            }

            sendMessage(pro137D20.fGetBytes());
        }


        Map<String, List<EtCrossLane>> map = new HashMap<>();
        for (EtCrossLane crossLane : crossLanes) {
            String lightNo = crossLane.getLightNo();
            if (map.containsKey(lightNo)) {
                map.get(lightNo).add(crossLane);
            } else {
                List<EtCrossLane> list = new ArrayList<>();
                list.add(crossLane);
                map.put(lightNo, list);
            }
        }
        for (String k : map.keySet()) {
            List<EtCrossLane> list = map.get(k);
            List<String> flows = new ArrayList<>();
            for (EtCrossLane etCrossLane : list) {
                String flow = etCrossLane.getFlow();
                flows.addAll(getFlows(flow));
            }

            String lightNo = k;
            Pro138D10 pro138D10 = new Pro138D10(slcIp);
            pro138D10.setCrossNo(crossNo);
            pro138D10.setType("1");
            pro138D10.setLightNo(lightNo);
            pro138D10.setFlows(flows);
            pro138D10.setDir(list.get(0).getDir());
            sendMessage(pro138D10.fGetBytes());


        }
        for (String k : map.keySet()) {
            List<EtCrossLane> list = map.get(k);
            List<String> flows = new ArrayList<>();
            Boolean isPed = false;
            for (EtCrossLane etCrossLane : list) {
                String flow = etCrossLane.getFlow();
                if (Integer.parseInt(flow) > 99) {
                    isPed = true;
                    break;
                }
            }
            if ("3".equals(k)) {
                System.out.println("");
            }
            String lightNo = k;
            Pro131D45 pro131D45 = new Pro131D45(slcIp);
            pro131D45.setCrossNo(crossNo);
            pro131D45.setLightNo(lightNo);

            if (isPed) {
                pro131D45.setIndex("1");
            } else {
                pro131D45.setIndex(list.get(0).getSeqNo());
            }

            sendMessage(pro131D45.fGetBytes());


        }

        Map<String, List<String>> dirLightMap = new HashMap<>();
        List<String> pedType = Arrays.asList("13", "14", "15");
        for (EtCrossLane crossLane : crossLanes) {
            String dir = crossLane.getDir();
            String pedFlag = pedType.contains(crossLane.getType()) ? "1" : "0";
            String k = dir + pedFlag;
            String lightNo = crossLane.getLightNo();
            if (dirLightMap.containsKey(k)) {
                List<String> list = dirLightMap.get(k);
                if (!list.contains(lightNo)) {
                    list.add(lightNo);
                }
            } else {
                List<String> list = new ArrayList<>();
                list.add(lightNo);
                dirLightMap.put(k, list);
            }
        }
        for (String k : dirLightMap.keySet()) {
            List<String> list = dirLightMap.get(k);
            boolean isPed = k.endsWith("1");
            for (String lightNo : list) {
                Pro131D45 pro131D45 = new Pro131D45(slcIp);
                pro131D45.setCrossNo(crossNo);
                pro131D45.setLightNo(lightNo);

                if (isPed) {
                    String seqNo = map.get(lightNo).get(0).getSeqNo();
                    String pedIndex = "2";
                    if ("100".equals(seqNo)) {
                        pedIndex = "2";
                    } else if ("101".equals(seqNo)) {
                        pedIndex = "1";
                    } else if ("102".equals(seqNo)) {
                        pedIndex = "3";
                    }
                    pro131D45.setIndex(pedIndex);

                } else {
                    pro131D45.setIndex(list.indexOf(lightNo) + 1 + "");
                }
                sendMessage(pro131D45.fGetBytes());
            }
        }

        pro129D10.setOrder("1");
        sendMessage(pro129D10.fGetBytes());
        pro129D10.setOrder("6");
        sendMessage(pro129D10.fGetBytes());
    }


    /**
     * 发送初始化相位灯组关系 （一一对应）
     */
    public void sendInitPhaseLightNo() {
        //查看当前相位灯组是否配置
        List<String> phaseNoList = new ArrayList<>();
        for (int i = 1; i < 33; i++) {
            phaseNoList.add(i + "");
        }
        Pro4S22 pro4S22 = new Pro4S22(slcIp);
        pro4S22.setCrossNo(crossNo);
        pro4S22.setPhaseNoList(phaseNoList);

        crossRepo.putQueue(crossNo, pro4S22.getReProtocol(), "", "");
        sendMessage(pro4S22.fGetBytes());
        Object object = crossRepo.getQueue(crossNo, pro4S22.getReProtocol(), "", "");

        Pro4R22 pro4R22 = (Pro4R22) object;
        if (pro4R22 == null || pro4R22.getPhaseLightMap() == null || pro4R22.getPhaseLightMap().size() == 0) {
            Map<String, List<Integer>> phaseLightMap = new HashMap<>();
            for (int i = 1; i < 33; i++) {
                phaseLightMap.put(i + "", Arrays.asList(i));
            }
            Pro4D22 pro4D22 = new Pro4D22(slcIp);
            pro4D22.setCrossNo(crossNo);
            pro4D22.setPhaseLightMap(phaseLightMap);
            sendMessage(pro4D22.fGetBytes());
        }


    }


    public void sendOverControlInfo() {
        Pro130D40 pro130D40 = new Pro130D40(slcIp);
        pro130D40.setCrossNo(crossNo);
        pro130D40.setFlowCycle("10");
        pro130D40.setOccupancyCycle("1");
        pro130D40.setContinueCongestionNum("0");
        pro130D40.setOverNum("0");
        sendMessage(pro130D40.fGetBytes());
    }

    public void getLaneInfo() {
        log.info("==========获取车道信息===========");
        for (int i = 1; i < 20; i++) {
            Pro137S20 pro137S20 = new Pro137S20(slcIp);
            pro137S20.setLaneNo(i + "");
            pro137S20.setCrossNo(crossNo);
            sendMessage(pro137S20.fGetBytes());
        }
        log.info("==========获取灯组信息===========");
        for (int i = 1; i < 20; i++) {
            Pro138S10 pro138S10 = new Pro138S10(slcIp);
            pro138S10.setLightNo(i + "");
            pro138S10.setCrossNo(crossNo);
            sendMessage(pro138S10.fGetBytes());
        }
        log.info("==========获取灯组-1信息===========");
        for (int i = 1; i < 20; i++) {
            Pro131S45 pro131S45 = new Pro131S45(slcIp);
            pro131S45.setLightNo(i + "");
            pro131S45.setCrossNo(crossNo);
            sendMessage(pro131S45.fGetBytes());
        }
    }

    public List<String> getFlows(String flow) {
        List<String> flows = new ArrayList<>();
        switch (flow) {
            //直
            case "11":
                flows.add("1");
                break;
            //左
            case "12":
                flows.add("0");
                break;
            //右
            case "13":
                flows.add("2");
                break;
            //直左
            case "21":
                flows.add("0");
                flows.add("1");
                break;
            //直右
            case "22":
                flows.add("1");
                flows.add("2");
                break;
            //左右
            case "23":
                flows.add("0");
                flows.add("2");
                break;
            //直左右
            case "24":
                flows.add("0");
                flows.add("1");
                flows.add("2");
                break;
            //掉头
            case "31":
                flows.add("3");
                break;
            //直掉头
            case "41":
                flows.add("1");
                flows.add("3");
                break;
            //左掉
            case "42":
                flows.add("0");
                flows.add("3");
                break;
            case "101":
                flows.add("4");
                break;
            case "102":
                flows.add("5");
                break;
            case "100":
                flows.add("4");
                flows.add("5");
                break;
            default:

        }
        return flows;
    }


    public String covertType(String type) {
        String laneType = "1";

        switch (type) {
            //普通机动车
            case "0":
                laneType = "1";
                break;
            //brt
            case "2":
                laneType = "2";
                break;
            //可变车道
            case "3":
                laneType = "4";
                break;
            //brt与可变
            case "4":
                laneType = "1";
                break;
            case "13":
                laneType = "13";
                break;
            case "14":
                laneType = "14";
                break;
            case "15":
                laneType = "15";
                break;

            default:
        }
        return laneType;
    }


    private String convertDetectType(String type) {
        if (StringUtils.isEmpty(type))
            return "1";
        switch (type) {
            case "21":
                //线圈
                return "1";
            case "22":
                //地磁
                return "3";
            case "23":
                //微波
                return "4";
            case "26":
                //视频
                return "2";
            case "37":
                //雷达
                return "5";
            default:
                return "1";
        }
    }

    /**
     * @param type 转换通道类型
     * @return
     */
    private String convertChannelType(String type) {
        switch (type) {
            case "2":
                //反溢控制
                return "2";
            case "3":
                //排队控制
                return "3";
            case "6":
                //感应
                return "1";
            default:
                return "0";
        }
    }


    /*
     * @Author Adam
     * @Description 设置检测器通道组态
     * @Date 11:02 2021/7/8
     * @Param []
     * @return void
     */
//    private void sendSetDetectChannel(String crossNo, String slcIp) {
//        List<EtCrossDectKind> crossDetectKind = crossService.getEtCrossDetectKind(Integer.parseInt(crossNo));
//        if (crossDetectKind == null || crossDetectKind.size() == 0) return;
//
//        List<List<Integer>> queDetectList = new ArrayList<>();
//        List<List<Integer>> overDetectList = new ArrayList<>();
//        List<List<Integer>> inductionDetectList = new ArrayList<>();
//        for (int i = 0; i < 64; i++) {
//            List<Integer> list = new ArrayList<>();
//            list.add(0);
//            queDetectList.add(i, list);
//            overDetectList.add(i, list);
//            inductionDetectList.add(i, list);
//        }
//        //1.下载相位检测器通道对应关系
//        if (crossDetectKind != null && crossDetectKind.size() > 0) {
//
//            //各类检测器通道参数集合
//            //排队
//            Map<Integer, List<Integer>> queDetectMap = new HashMap<>();
//            //反溢
//            Map<Integer, List<Integer>> overDetectMap = new HashMap<>();
//            //感应（一般检测器、紧急检测器）
//            Map<Integer, List<Integer>> inductionDetectMap = new HashMap<>();
//
//            for (int i = 0; i < crossDetectKind.size(); i++) {
//                EtCrossDectKind item = crossDetectKind.get(i);
//                //检测器类型
//                int detKind = Integer.parseInt(item.getDetKind());
//                if (detKind != 0) {
//                    //相位编号
//                    int signalPort = item.getSignalPort();
//                    //通道编号
//                    int channelNo = Integer.parseInt(item.getChannelNo());
//                    switch (detKind) {
//                        case 3:
//                            //排队
//                            //排队检测器编号
//                            queDetectMap = setDetectChannelNo(queDetectMap, signalPort, channelNo);
//                            break;
//                        case 2:
//                            //反溢
//                            overDetectMap = setDetectChannelNo(overDetectMap, signalPort, channelNo);
//                            break;
//                        case 6:
//                            //感应
//                        case 4:
//                            //紧急
//                            inductionDetectMap = setDetectChannelNo(inductionDetectMap, signalPort, channelNo);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//
//            if (queDetectMap != null && queDetectMap.size() > 0) {
//                for (int signalPort : queDetectMap.keySet()) {
//                    List<Integer> listChannelNo = queDetectMap.get(signalPort);
//                    queDetectList.set(signalPort - 1, listChannelNo);
//                }
//
//            }
//            if (overDetectMap != null && overDetectMap.size() > 0) {
//                for (int signalPort : overDetectMap.keySet()) {
//                    List<Integer> listChannelNo = overDetectMap.get(signalPort);
//                    overDetectList.set(signalPort - 1, listChannelNo);
//                }
//            }
//            if (inductionDetectMap != null && inductionDetectMap.size() > 0) {
//                for (int signalPort : inductionDetectMap.keySet()) {
//                    List<Integer> listChannelNo = inductionDetectMap.get(signalPort);
//                    inductionDetectList.set(signalPort - 1, listChannelNo);
//                }
//            }
//
//        }
//
//        Pro4D20 pro1 = new Pro4D20(slcIp);
//        pro1.setCrossNo(crossNo);
//        //感应
//        pro1.setInductionDetectList(inductionDetectList);
//        sendSlcIpMsg(slcIp, pro1.fGetBytes());
//
//        Pro146D10 pro2 = new Pro146D10(slcIp);
//        pro2.setPhaseNo(crossNo);
//        pro2.setQueueDetectNoList(queDetectList);
//        pro2.setReverseDetectNoList(overDetectList);
//        sendSlcIpMsg(slcIp, pro2.fGetBytes());
//
//
//    }

    /**
     * 设置各类检测器通道参数集合
     *
     * @param detectMap
     * @param signalPort
     * @param channelNo
     * @return
     */
    private static Map<Integer, List<Integer>> setDetectChannelNo(Map<Integer, List<Integer>> detectMap,
                                                                  int signalPort, int channelNo) {
        if (detectMap.size() > 0 && detectMap.containsKey(signalPort)) {
            List<Integer> list = detectMap.get(signalPort);
            if (list != null && list.size() > 0) {
                list.add(channelNo);
            } else {
                list = new ArrayList<>();
                list.add(channelNo);
            }
            detectMap.put(signalPort, list);
        } else {
            List<Integer> list = new ArrayList<>();
            list.add(channelNo);
            detectMap.put(signalPort, list);
        }
        return detectMap;
    }


}
