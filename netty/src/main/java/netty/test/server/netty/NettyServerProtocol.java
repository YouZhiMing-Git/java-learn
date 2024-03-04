package netty.test.server.netty;

import com.ehualu.eloc.common.config.ServiceConfig;
import com.ehualu.eloc.common.entity.redis.RdCrossTimingPlan;
import com.ehualu.eloc.common.infrastructure.redis.RedisUtil;
import com.ehualu.eloc.common.protocolgb.ControlState;
import com.ehualu.eloc.common.protocolgb.LightBean;
import com.ehualu.eloc.common.protocolgb.PhaseStageDetail;
import com.ehualu.eloc.common.protocolgb.recv.*;
import com.ehualu.eloc.common.protocolgb.send.*;
import com.ehualu.eloc.common.util.CommonUtil;
import com.ehualu.eloc.common.util.TimeUtil;
import com.ehualu.eloc.sts.system.SystemComponent;
import com.ehualu.eloc.sts.system.repository.CrossRepo;
import com.ehualu.eloc.sts.system.repository.SystemRepo;
import com.ehualu.eloc.sts.util.ProUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Adam
 * @Date 2021/6/17 10:38
 * @Description Socket通信协议解析类
 */
@Slf4j
@Component
public class NettyServerProtocol {
    @Autowired
    private CrossRepo crossRepo;

    @Autowired
    private SystemComponent systemComponent;

    @Autowired
    private SystemRepo systemRepo;

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private RedisUtil redisUtil;

    /*
     * @Author Adam
     * @Description 协议解析
     * @Date 13:47 2021/6/17
     * @Param [slcIp, byteBuf]
     * @return void
     */
    public void transferProtocol(String slcIp, ByteBuf byteBuf) {
        byte[] bTemp = splitReceiveData(byteBuf);

        if (bTemp == null || bTemp.length == 0) {
            return;
        }
//        log.info("接收： " + slcIp + "  " + CommonUtil.byte2String(bTemp));
        //协议头
        String proHead = ProUtil.getProHead(bTemp);
        //验证协议头
        if (!ProUtil.PRO_HEAD.equals(proHead)) {
            return;
        }
        //验证CRC-16状态码
        if (!ProUtil.vilifyProCrc(bTemp)) {
            return;
        }

        //协议编号
        String protocol = "";
        if (CommonUtil.byte2String(bTemp).equals("7E,00,0C,10,00,01,00,00,00,01,01,00,80,09,91,7D")) {
//            System.out.println("收到心跳协议："+ CommonUtil.byte2String(bTemp));
            return;
        }
        try {
            protocol = ProUtil.getPro(bTemp);
        } catch (Exception e) {
//            System.out.println("收到的是心跳：" + CommonUtil.byte2String(bTemp));
            return;
        }

        //获取信号机 IP 对应的 crossNo
        List<String> crossNoList = new ArrayList<>();
        if (systemComponent.getChannelGroup().containsKey(slcIp)) {
            NettyServerSrc nettyServerSrc = systemComponent.getChannelGroup().get(slcIp);
            if (nettyServerSrc != null) {
                crossNoList = nettyServerSrc.getCrossNoList();
                //更新数据接收时间
                nettyServerSrc.setReceiveTime(TimeUtil.getNowTimeStampInt());
            }
        }

        if (crossNoList == null) {
            crossNoList = new ArrayList<>();
        }

        //协议编码
        if (!"137R10".equals(protocol) && crossNoList.size() == 0) {
            //非路口资源回报,路口信号机IP不在CCU里
            return;
        }

//        if (bTemp[16] == 0x05)
//            log.info("接收： " + slcIp + "  " + CommonUtil.byte2String(bTemp));

        //信号机回复类型
        String proResultType = ProUtil.getProFrameStreamType(bTemp, 12);

        switch (proResultType) {
            case "20":
                //查询正常
                switch (protocol) {
                    case "1R2551":
                        //回报烧录程序版本
                        Pro1R2551 pro1R2551 = new Pro1R2551(slcIp, bTemp);
                        pro1R2551.setCrossNoList(crossNoList);
                        if (!crossRepo.putQueueIfApi(slcIp, pro1R2551.getProtocol(), pro1R2551)) {
                            systemRepo.excuteSendThread(pro1R2551);
                        }
                        break;
                    case "2R2550":
                        //回报MAC地址
                        Pro2R2550 pro2R2550 = new Pro2R2550(slcIp, bTemp);
                        pro2R2550.setCrossNoList(crossNoList);
                        if (!crossRepo.putQueueIfApi(slcIp, pro2R2550.getProtocol(), pro2R2550)) {
                            systemRepo.excuteSendThread(pro2R2550);
                        }

                        break;
                    case "3R10":
                        //回报实际灯组数
                        Pro3R10 pro3R10 = new Pro3R10(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro3R10.getProtocol(), pro3R10);
                        break;
                    case "3R20":
                        //回报灯组编号对应的灯组类型
                        Pro3R20 pro3R20 = new Pro3R20(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro3R20.getProtocol(), pro3R20);
                        break;
                    case "3R30":
                        //回报灯组状态
                        Pro3R30 pro3R30 = new Pro3R30(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro3R30.getProtocol(), pro3R30);
                        break;
                    case "4R20":
                        //回报相位配置表
                        Pro4R20 pro4R20 = new Pro4R20(slcIp, bTemp);
                        pro4R20.setCrossNoList(crossNoList);
//                        log.info("接收： " + slcIp + "  " + JSON.toJSONString(pro4R20));
                        if (!crossRepo.putQueueIfApi(slcIp, pro4R20.getProtocol(), "", pro4R20.getPhaseNo(), pro4R20)) {
                            systemRepo.excuteSendThread(pro4R20);
                        }
                        break;
                    case "6R22":
                        //回报相位阶段中包含的相位
                        Pro6R22 pro6R22 = new Pro6R22(slcIp, bTemp);
                        pro6R22.setCrossNoList(crossNoList);
                        String phaseStageNo = pro6R22.getPhaseStageNo();
                        if (phaseStageNo == null) {
                            phaseStageNo = "0";
                        }
                        if (!crossRepo.putQueueIfApi(slcIp, pro6R22.getProtocol(), "", phaseStageNo, pro6R22)) {
                            if (!"0".equals(phaseStageNo)) {
                                //相位编号
                                List<String> phaseNoList = pro6R22.getPhaseNoList();
                                if (phaseNoList != null && phaseNoList.size() > 0) {
                                    //获取相位配置参数
                                    getPhase(phaseNoList, slcIp, crossNoList);
//                                    //该相阶中的机动车相位
//                                    String flowPhase = CommonUtil.getFlowPhase(phaseNoList);
//                                    //获取相阶中的行人相位
//                                    String pedPhase = CommonUtil.getPedPhase(phaseNoList);
//                                    //该相阶中，既有机动车，也有行人相位
//                                    if (!"0".equals(flowPhase) && !"0".equals(pedPhase)) {
//                                        Pro4S20 pro4S20 = new Pro4S20(slcIp);
//                                        pro4S20.setPhaseNo(flowPhase);
//                                        systemComponent.sendSlcIpMsg(pro6R22.getSlcIp(), pro4S20.fGetBytes());
//                                        Pro4S20 proPed4S20 = new Pro4S20(slcIp);
//                                        proPed4S20.setPhaseNo(pedPhase);
//                                        systemComponent.sendSlcIpMsg(pro6R22.getSlcIp(), proPed4S20.fGetBytes());
//                                    } else {
//                                        String phaseNo = phaseNoList.get(0);
//                                        if (phaseNo != null && !"".equals(phaseNo) && !"0".equals(phaseNo)) {
//                                            Pro4S20 pro4S20 = new Pro4S20(slcIp);
//                                            pro4S20.setPhaseNo(phaseNo);
//                                            systemComponent.sendSlcIpMsg(pro6R22.getSlcIp(), pro4S20.fGetBytes());
//                                        }
//                                    }
                                }
                                systemRepo.excuteSendThread(pro6R22);
                            }
                        }
                        break;
                    case "6R23":
                        //回报相位阶段库中相位晚起时间
                        Pro6R23 pro6R23 = new Pro6R23(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro6R23.getProtocol(), pro6R23);
                        break;
                    case "6R24":
                        //回报相位阶段库中相位早闭时间
                        Pro6R24 pro6R24 = new Pro6R24(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro6R24.getProtocol(), pro6R24);
                        break;
                    case "7R10":
                        //回报相位冲突表
                        Pro7R10 pro7R10 = new Pro7R10(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro7R10.getProtocol(), "", pro7R10.getPhaseNo(), pro7R10);
                        break;
                    case "9R20":
                        //回报方案信息
                        Pro9R20 pro9R20 = new Pro9R20(slcIp, bTemp);
                        setCrossNo(pro9R20, crossNoList);
                        if (!crossRepo.putQueueIfApi(slcIp, pro9R20.getProtocol(), pro9R20.getCrossIdNo(), pro9R20.getPlanNo(), pro9R20)) {
                            //获取相阶包含相位
                            List<PhaseStageDetail> phaseStageDetails = pro9R20.getPhaseStageDetails();
                            phaseStageDetails.forEach(item -> {
                                Pro6S22 pro6S22 = new Pro6S22(slcIp);
                                pro6S22.setCrossNo(pro9R20.getCrossNo());
                                pro6S22.setPhaseStageNo(item.getPhaseStageNo());
                                systemComponent.sendCrossMsg(pro9R20.getCrossNo(), pro6S22.fGetBytes());
                            });
                            systemRepo.excuteSendThread(pro9R20);
                        }
                        break;
                    case "9R28":
                        //回报方案相位阶段出现类型链
                        Pro9R28 pro9R28 = new Pro9R28(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro9R28.getProtocol(), pro9R28);
                        break;
                    case "11R20":
                        //回报日计划配置
                        Pro11R20 pro11R20 = new Pro11R20(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro11R20.getProtocol(), pro11R20.getCrossIdNo(), pro11R20.getDayPlanNo(), pro11R20);
                        break;
                    case "12R20":
                        //回报调度表配置
                        Pro12R20 pro12R20 = new Pro12R20(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro12R20.getProtocol(), pro12R20.getCrossIdNo(), pro12R20.getScheduleNo(), pro12R20);
                        break;
                    case "12R25":
                        //回报所有调度表编号对应的月份值
                        Pro12R25 pro12R25 = new Pro12R25(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro12R25.getProtocol(), pro12R25);
                        break;
                    case "12R27":
                        //回报所有调度表编号对应的日计划编号
                        Pro12R27 pro12R27 = new Pro12R27(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro12R27.getProtocol(), pro12R27);
                        break;
                    case "12R2550":
                        //回报使用的调度表编号
                        Pro12R2550 pro12R2550 = new Pro12R2550(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro12R2550.getProtocol(), pro12R2550.getCrossIdNo(), pro12R2550);
                        break;
                    case "13R20":
                        //控制模式
                        //回报当前运行状态表
                        Pro13R20 pro13R20 = new Pro13R20(slcIp, bTemp);
                        setCrossNo(pro13R20, crossNoList);
                        if (!crossRepo.putQueueIfApi(slcIp, pro13R20.getProtocol(), pro13R20.getCrossIdNo(), pro13R20)) {
                            systemRepo.excuteSendThread(pro13R20);
                        }
                        break;
                    case "13R23":
                        //回报当前运行方案
                        Pro13R23 pro13R23 = new Pro13R23(slcIp, bTemp);
                        setCrossNo(pro13R23, crossNoList);
                        String crossNo = pro13R23.getCrossNo();
                        if (!crossRepo.putQueueIfApi(slcIp, pro13R23.getProtocol(), pro13R23.getCrossIdNo(), pro13R23)) {
                            if (crossNo != null) {
                                String slcPlanNo = pro13R23.getSlcPlanNo();
                                Pro9S20 pro9S20 = new Pro9S20(slcIp);
                                pro9S20.setCrossNo(crossNo);
                                pro9S20.setCrossIdNo(pro13R23.getCrossIdNo());
                                pro9S20.setPlanNo(slcPlanNo);
                                systemComponent.sendCrossMsg(crossNo, pro9S20.fGetBytes());
                            }
                            systemRepo.excuteSendThread(pro13R23);
                        }
                        break;
                    case "13R111":
                        //回报信号机本地时间
                        Pro13R111 pro13R111 = new Pro13R111(slcIp, bTemp);
//                        System.out.println("收到协议13R111：" + pro13R111.toString());
                        crossRepo.putQueueIfApi(slcIp, pro13R111.getProtocol(), pro13R111);
                        break;
                    case "17R2550":
                        //禁止现场手动状态
                        //回报是否允许现场手动状态
                        Pro17R2550 pro17R2550 = new Pro17R2550(slcIp, bTemp);
                        pro17R2550.setCrossNoList(crossNoList);
                        if (!crossRepo.putQueueIfApi(slcIp, pro17R2550.getProtocol(), pro17R2550)) {
                            systemRepo.excuteSendThread(pro17R2550);
                        }
                        break;
                    case "129R10":
                        //回报倒计时开启
                        Pro129R10 pro129R10 = new Pro129R10(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro129R10.getProtocol(), pro129R10);
                        break;
                    case "129R20":
                        //回报倒计时方式
                        Pro129R20 pro129R20 = new Pro129R20(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro129R20.getProtocol(), pro129R20);
                        break;
                    case "129R40":
                        //回报脉冲宽度
                        Pro129R40 pro129R40 = new Pro129R40(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro129R40.getProtocol(), pro129R40);
                        break;
                    case "129R50":
                        //回报倒计时显示时长
                        Pro129R50 pro129R50 = new Pro129R50(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro129R50.getProtocol(), pro129R50);
                        break;
                    case "129R70":
                        //回报波特率
                        Pro129R70 pro129R70 = new Pro129R70(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro129R70.getProtocol(), pro129R70);
                        break;
                    case "132R10":
                        //回报内置模块安装状态
                        Pro132R10 pro132R10 = new Pro132R10(slcIp, bTemp);
                        pro132R10.setCrossNoList(crossNoList);
                        if (!crossRepo.putQueueIfApi(slcIp, pro132R10.getProtocol(), pro132R10)) {
                            systemRepo.excuteSendThread(pro132R10);
                        }
                        break;
                    case "137R10":
                        //路口资源
                        //回报一带多路口资源配置
                        Pro137R10 pro137R10 = new Pro137R10(slcIp, bTemp);
                        setCrossNo(pro137R10, crossNoList);
                        if (!crossRepo.putQueueIfApi(slcIp, pro137R10.getProtocol(), pro137R10.getCrossIdNo(), pro137R10)) {
                            systemRepo.excuteSendThread(pro137R10);
                        }
                        break;
                    case "140R10":
                        //灯态
                        break;
                    case "140R20":
                        //硬件故障
                        break;
                    case "140R30":
                        //控制策略
                        Pro140U30 pro140U30 = new Pro140U30(slcIp, bTemp);
                        setCrossNo(pro140U30, crossNoList);
                        pro140U30.setProtocol("140R30");
                        crossRepo.putQueueIfApi(slcIp, pro140U30.getProtocol(), pro140U30.getCrossIdNo(), pro140U30);
                        systemRepo.excuteSendThread(pro140U30);

                        break;
                    case "140R40":
                        //硬件环境故障
                        Pro140U40 pro140U40 = new Pro140U40(slcIp, bTemp);
                        pro140U40.setCrossNoList(crossNoList);
                        systemRepo.excuteSendThread(pro140U40);
                        break;
                    case "143R10":
                        //回报安全信息
                        Pro143R10 pro143R10 = new Pro143R10(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro143R10.getProtocol(), pro143R10);
                        break;
                    case "143R20":
                        //回报安全信息
                        Pro143R20 pro143R20 = new Pro143R20(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro143R20.getProtocol(), pro143R20);
                        break;
                    case "143R60":
                        //回报信号机认证信息
                        Pro143R60 pro143R60 = new Pro143R60(slcIp, bTemp);
                        crossRepo.putQueueIfApi(slcIp, pro143R60.getProtocol(), pro143R60);
                        break;
                    default:
                        break;
                }
                break;
            case "40":
//                log.info("接收到设置成功消息： " + slcIp + ",  协议格式：" + protocol + "，协议内容：" + CommonUtil.byte2String(bTemp));
                //设置正常
                Pro0D8X pro0D80 = new Pro0D8X(slcIp, protocol, bTemp);
                pro0D80.setResult(true);
                crossRepo.putQueueIfApi(slcIp, pro0D80.getReProtocol(), pro0D80);
                break;
            case "21":
                log.info("接收到查询失败协议： " + slcIp + ",  协议格式：" + protocol + "，协议内容：" + CommonUtil.byte2String(bTemp));
                //查询出错
                break;
            case "41":
                //设置出错
                log.info("接收到设置失败消息： " + slcIp + ",  协议格式：" + protocol + "，协议内容：" + CommonUtil.byte2String(bTemp));
                Pro0D8X pro0D81 = new Pro0D8X(slcIp, protocol, bTemp);
                pro0D81.setResult(false);
                crossRepo.putQueueIfApi(slcIp, pro0D81.getReProtocol(), pro0D81);
                break;
            case "60":
                //主动上报
                switch (protocol) {
                    case "140U10":
                        //灯态
                        Pro140U10 pro140U10 = new Pro140U10(slcIp, bTemp);
                        setCrossNo(pro140U10, crossNoList);
                        systemRepo.excuteSendThread(pro140U10);
                        //    getDetectData(crossNoList.get(0),slcIp,pro140U10);
                        break;
                    case "140U20":
                        //硬件故障
                        Pro140U20 pro140U20 = new Pro140U20(slcIp, bTemp);
                        pro140U20.setCrossNoList(crossNoList);
                        systemRepo.excuteSendThread(pro140U20);
                        break;
                    case "140U30":
                        //控制策略
                        Pro140U30 pro140U30 = new Pro140U30(slcIp, bTemp);
                        setCrossNo(pro140U30, crossNoList);
                        if (pro140U30 != null && pro140U30.getCrossNo() != null && pro140U30.getControlState() != null) {
                            ControlState controlState = redisUtil.getControlState(pro140U30.getCrossNo());
                            if (pro140U30.getControlState().equals(controlState)) return;
                        }
                        systemRepo.excuteSendThread(pro140U30);
//                        System.out.println("收到140U30：");
                        if (crossNoList != null && crossNoList.size() > 0) {
                            for (int i = 0; i < crossNoList.size(); i++) {
                                String slcOrder = redisUtil.getSlcOrder(crossNoList.get(i));
                                //控制模式
                                Pro13S20 pro13S20 = new Pro13S20(slcIp);
                                pro13S20.setCrossNo(crossNoList.get(i));
                                pro13S20.setCrossIdNo(slcOrder);
                                systemComponent.sendCrossMsg(crossNoList.get(i), pro13S20.fGetBytes());
                            }
                        }
                        break;
                    case "140U40":
                        //硬件环境故障
                        Pro140U40 pro140U40 = new Pro140U40(slcIp, bTemp);
                        pro140U40.setCrossNoList(crossNoList);
                        systemRepo.excuteSendThread(pro140U40);
                        break;
                    case "128U41":
                        //信号机程序更新成功
                        break;
                    case "144U20":
                        //电子锁回复认证信息成功
                        Pro144U20 pro144U20 = new Pro144U20(slcIp, bTemp);
                        crossRepo.putQueue(slcIp, "144U20", "", "");
                        crossRepo.putQueueIfApi(slcIp, pro144U20.getProtocol(), pro144U20);
                        break;
                    case "148U10":
                        //信号机故障上报
                        Pro148U10 pro148U10 = new Pro148U10(slcIp, bTemp);
                        pro148U10.setCrossNoList(crossNoList);
                        systemRepo.excuteSendThread(pro148U10);
                        break;
                    case "148U20":
                        //信号机参数修改日志上报
                        Pro148U20 pro148U20 = new Pro148U20(slcIp, bTemp);
                        pro148U20.setCrossNoList(crossNoList);
                        systemRepo.excuteSendThread(pro148U20);
                        break;
                    case "5U71":
                        //920协议检测器数据上报
                        if ((serviceConfig.getDetTypeGb() != 0)) {
                            Pro5U71 pro5U71 = new Pro5U71(slcIp, bTemp);
                            pro5U71.setCrossNoList(crossNoList);
                            systemRepo.excuteSendDetectThread(pro5U71);
                        }
                        break;

                    case "5R2490":
                        Pro5R2490 pro5R2490 = new Pro5R2490(slcIp, bTemp);
                        systemRepo.executeDetectData(pro5R2490);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    /*
     * @Author Adam
     * @Description 获取相位阶段内机动车相位和行人相位配置参数
     * @Date 9:29 2021/11/26
     * @Param [phaseNoList, slcIp, crossNoList]
     * @return void
     */
    private void getPhase(List<String> phaseNoList, String slcIp, List<String> crossNoList) {
        for (int i = 0; i < crossNoList.size(); i++) {
            String crossNo = crossNoList.get(i);
            Map<String, Integer> crossPort3 = redisUtil.getCrossPort3(crossNo);
            if (crossPort3 == null || crossPort3.size() == 0) {
                continue;
            }

            //该相阶中的机动车相位
            String flowPhase = CommonUtil.getFlowPhase(phaseNoList, crossPort3);
            //获取相阶中的行人相位
            String pedPhase = CommonUtil.getPedPhase(phaseNoList, crossPort3);
            //该相阶中，既有机动车，也有行人相位
            if (!"0".equals(flowPhase) && !"0".equals(pedPhase)) {
                Pro4S20 pro4S20 = new Pro4S20(slcIp);
                pro4S20.setPhaseNo(flowPhase);
                systemComponent.sendSlcIpMsg(slcIp, pro4S20.fGetBytes());
                Pro4S20 proPed4S20 = new Pro4S20(slcIp);
                proPed4S20.setPhaseNo(pedPhase);
                systemComponent.sendSlcIpMsg(slcIp, proPed4S20.fGetBytes());
            } else {
                String phaseNo = phaseNoList.get(0);
                if (phaseNo != null && !"".equals(phaseNo) && !"0".equals(phaseNo)) {
                    Pro4S20 pro4S20 = new Pro4S20(slcIp);
                    pro4S20.setPhaseNo(phaseNo);
                    systemComponent.sendSlcIpMsg(slcIp, pro4S20.fGetBytes());
                }
            }
        }
    }

    /*
     * @Author Adam
     * @Description 为返回协议设置路口编号
     * @Date 10:14 2021/6/30
     * @Param [pro, crossNoList]
     * @return void
     */
    private void setCrossNo(Object pro, List<String> crossNoList) {
        Class clazz = pro.getClass();
        try {
            for (int i = 0; i < crossNoList.size(); i++) {
                String crossNo = crossNoList.get(i);
                String slcOrder = redisUtil.getSlcOrder(crossNo);
                String crossIdNo = (String) clazz.getMethod("getCrossIdNo").invoke(pro);
                if (crossIdNo.equals(slcOrder)) {
                    clazz.getMethod("setCrossNo", String.class).invoke(pro, crossNo);
                    break;
                }
            }
            String crossNo = (String) clazz.getMethod("getCrossNo").invoke(pro);
            if (crossNo == null) {
//                clazz.getMethod("setCrossNoList", List.class).invoke(pro, crossNoList);
            } else {
//                System.out.println("我的路口编号：" + crossNo);
            }
        } catch (Exception e) {
            System.out.println("出现异常方法！");
            e.printStackTrace();
        }

    }


    private byte[] splitReceiveData(ByteBuf byteBuf) {

        int byteSize = byteBuf.readableBytes();
        byte[] buf = new byte[byteSize];
        byteBuf.readBytes(buf);


        int protocolLength = 0;
        if (protocolLength == 0) {
            protocolLength = byteSize;
        }
        if (protocolLength != 0) {
            List<Integer> tempIntList = new ArrayList<>();
            byte[] bTemp = new byte[protocolLength];
            for (int i = 0; i < protocolLength; i++) {
                bTemp[i] = buf[i];
                if ((i + 1) < protocolLength) {
                    if (buf[i] == (byte) 0x5C && ((buf[i + 1] == (byte) 0x5C && buf[i + 2] != (byte) 0x7D) || (buf[i + 1] == (byte) 0x7D) || (buf[i + 1] == (byte) 0x7E))) {
                        if (!((buf[i] == (byte) 0x5C) && (buf[i + 1] == (byte) 0x5C) && (buf[i + 2] == (byte) 0x5C) && (buf[i + 3] == (byte) 0x5C))) {
                            tempIntList.add(i + 1);
                        }
                    }
                }
            }
            List<byte[]> listTemp = new ArrayList<byte[]>();

            for (int i = 0; i < tempIntList.size(); i++) {
                int beginIndex = 0;
                int endIndex = 0;
                int count1 = 0;
                if (i == 0) {
                    beginIndex = 0;
                    endIndex = tempIntList.get(0);
                    count1 = endIndex - beginIndex;
                } else {
                    beginIndex = tempIntList.get(i - 1) + 1;
                    endIndex = tempIntList.get(i);
                    count1 = endIndex - beginIndex;
                }
                //非最后一组
                byte[] bytes = subBytes(bTemp, beginIndex, count1);
                listTemp.add(bytes);
            }

            //最后剩余byte段
            if (tempIntList.size() > 0) {
                Integer integer = tempIntList.get(tempIntList.size() - 1);
                byte[] bytesTemp = new byte[protocolLength - integer - 1];
                for (int i = integer + 1; i < protocolLength; i++) {
                    bytesTemp[i - (integer + 1)] = bTemp[i];
                }
                listTemp.add(bytesTemp);
            }

            if (listTemp.size() != 0) {
                byte[] result = listTemp.get(0);
                for (int i = 1; i < listTemp.size(); i++) {
                    byte[] bytes = listTemp.get(i);
                    result = combineListBytes(result, bytes);
                }
                return result;
            } else {
                return bTemp;
            }
        } else {
            return null;
        }
    }

    private byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        int size = begin + count;
        for (int i = begin; i < size; i++) {
            if (i == size - 1) {
                bs[i - begin] = src[i + 1];
            } else {
                bs[i - begin] = src[i];
            }
        }
        return bs;
    }

    /**
     * 合成 byte[]
     *
     * @param bytes1 前 byte[]
     * @param bytes2 后 byte[]
     * @return
     */
    private byte[] combineListBytes(byte[] bytes1, byte[] bytes2) {
        int length1 = bytes1.length;
        int length2 = bytes2.length;
        byte[] result = new byte[length1 + length2];
        for (int i = 0; i < length1; i++) {
            result[i] = bytes1[i];
        }
        for (int j = 0; j < length2; j++) {
            result[length1 + j] = bytes2[j];
        }
        return result;
    }


    /**
     * @param crossNo
     * @param pro140U10 当前相阶为最后一相阶，且当前为黄灯
     *                  获取当前周期的检测器数据
     */
    private void getDetectData(String crossNo, String slcIp, Pro140U10 pro140U10) {
        String phaseNo = pro140U10.getPhaseStageSeqNo();
        RdCrossTimingPlan rdCrossTimingPlan = redisUtil.getCrossCurrentTimingPlan(crossNo);
        Map<Integer, LightBean> lightBeans = pro140U10.getLightBeans();
        boolean isYellow = false;
        for (Integer i : lightBeans.keySet()) {
            if ("YELLOW".equals(lightBeans.get(i).getLightState())) {
                isYellow = true;
                break;
            }
        }
        if (rdCrossTimingPlan.getTimingPlanInfo() == null || rdCrossTimingPlan.getTimingPlanInfo().size() == 0) {
            return;
        }
        if (Integer.parseInt(phaseNo) == rdCrossTimingPlan.getTimingPlanInfo().size() && isYellow) {
            //获取检测器数据
            Pro5S2490 pro5S2490 = new Pro5S2490(slcIp);
            systemComponent.sendCrossMsg(crossNo, pro5S2490.fGetBytes());
        }

    }
}
