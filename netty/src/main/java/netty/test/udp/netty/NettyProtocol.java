package netty.test.udp.netty;

import com.ehualu.eloc.common.config.ServiceConfig;
import com.ehualu.eloc.common.infrastructure.redis.RedisUtil;
import com.ehualu.eloc.common.protocolgb.ControlState;
import com.ehualu.eloc.common.protocolhaixin.common.PhaseStageDetail;
import com.ehualu.eloc.common.protocolhaixin.recv.*;
import com.ehualu.eloc.common.protocolhaixin.send.*;
import com.ehualu.eloc.common.util.CommonUtil;
import com.ehualu.eloc.common.util.TimeUtil;
import com.ehualu.eloc.sts.config.log.LogConfig;
import com.ehualu.eloc.sts.system.SystemUdpComponent;
import com.ehualu.eloc.sts.system.repository.CrossRepo;
import com.ehualu.eloc.sts.system.repository.SystemRepo;
import com.ehualu.eloc.sts.util.LogUtil;
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
public class NettyProtocol {
    @Autowired
    private CrossRepo crossRepo;

    @Autowired
    private SystemUdpComponent systemComponent;

    @Autowired
    private SystemRepo systemRepo;

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LogConfig logConfig;

    /*
     * @Author Adam
     * @Description 协议解析
     * @Date 13:47 2021/6/17
     * @Param [slcIp, byteBuf]
     * @return void
     */
    public void transferProtocol(String crossNo, ByteBuf byteBuf) {
        redisUtil.setCrossSlcReceiveTime(crossNo, TimeUtil.getNowTimeStampInt());
        byte[] bTemp = splitReceiveData(byteBuf);

        if (bTemp == null || bTemp.length == 0) {
            return;
        }
        //协议头
        String proHead = ProUtil.getProHead(bTemp);
        //验证协议头
        if (!ProUtil.PRO_HEAD.equals(proHead)) {
            return;
        }
        if (CommonUtil.byte2String(bTemp).equals("7E,00,0C,10,00,01,00,00,00,00,01,00,80,0D,D5,7D")) {
            return;
        }
        //协议编号
        String protocol = "";
        try {
            protocol = ProUtil.getPro(bTemp);
        } catch (Exception e) {
//            System.out.println("收到的是心跳：" + CommonUtil.byte2String(bTemp));
            log.info("解析协议名称出现异常：" + CommonUtil.byte2String(bTemp));
            return;
        }


        //信号机回复类型
        String proResultType = ProUtil.getProFrameStreamType(bTemp, 12);

        //信号机IP
        String slcIp = redisUtil.getSlcIp(crossNo);
        if ("136U10".equals(protocol)) {
            return;
        }
        if ("131D45".equals(protocol)) {
            return;
        }
//        if ("DEBUG".equals(logConfig.getLevel())) {
//            log.info("接收： " + crossNo + "  类型：" + proResultType + "  协议：" + protocol + ": " + CommonUtil.byte2String(bTemp));
//        }
//        log.info("接收： " + crossNo + "  类型：" + proResultType + "  协议：" + protocol + ": " + CommonUtil.byte2String(bTemp));

        LogUtil.info(crossNo, protocol, bTemp, 2);

        switch (proResultType) {
            case "20":
                //查询正常
                switch (protocol) {

                    case "1R20":
                        //当前版本
                        Pro1R20 pro1R20 = new Pro1R20(slcIp, bTemp);
                        pro1R20.setCrossNo(crossNo);
//                        crossRepo.putQueueIfApi(slcIp, pro1R20.getProtocol(), pro1R20);
                        if (!crossRepo.putQueueIfApi(crossNo, pro1R20.getProtocol(), pro1R20)) {
                            systemRepo.excuteSendThread(pro1R20);
                        }
                        break;
                    case "3R10":
                        //回报实际灯组数
                        Pro3R10 pro3R10 = new Pro3R10(slcIp, bTemp);
                        pro3R10.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro3R10.getProtocol(), pro3R10);
                        break;
                    case "3R20":
                        //回报灯组编号对应的灯组类型
                        Pro3R20 pro3R20 = new Pro3R20(slcIp, bTemp);
                        pro3R20.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro3R20.getProtocol(), pro3R20);
                        break;
                    case "3R30":
                        //回报灯组状态
                        Pro3R30 pro3R30 = new Pro3R30(slcIp, bTemp);
                        pro3R30.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro3R30.getProtocol(), pro3R30);
                        break;
                    case "4R20":
                        //回报相位配置表
                        Pro4R20 pro4R20 = new Pro4R20(slcIp, bTemp);
                        pro4R20.setCrossNo(crossNo);
//                        pro4R20.setCrossNoList(crossNoList);
//                        log.info("接收： " + slcIp + "  " + JSON.toJSONString(pro4R20));
                        if (!crossRepo.putQueueIfApi(crossNo, pro4R20.getProtocol(), "", pro4R20.getPhaseNo(), pro4R20)) {
                            systemRepo.excuteSendThread(pro4R20);
                        }
                        break;
                    case "4R22":
                        //回报相位灯组配置
                        Pro4R22 pro4R22 = new Pro4R22(slcIp, bTemp);
                        pro4R22.setCrossNo(crossNo);
//                        pro4R22.setCrossNoList(crossNoList);
                        if (!crossRepo.putQueueIfApi(crossNo, pro4R22.getProtocol(), pro4R22)) {
                            systemRepo.excuteSendThread(pro4R22);
                        }
                        break;
                    case "4R231":
                        Pro4R231 pro4R231 = new Pro4R231(slcIp, bTemp);
                        pro4R231.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro4R231.getProtocol(), pro4R231);
                        break;
                    case "5R20":
                        //回报检测器配置信息
                        Pro5R20 pro5R20 = new Pro5R20(slcIp, bTemp);
                        pro5R20.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro5R20.getProtocol(), pro5R20);
                        break;
                    case "6R22":
                        //回报相位阶段中包含的相位
                        Pro6R22 pro6R22 = new Pro6R22(slcIp, bTemp);
                        pro6R22.setCrossNo(crossNo);
                        String phaseStageNo = pro6R22.getPhaseStageNo();
                        if (phaseStageNo == null) {
                            phaseStageNo = "0";
                        }
                        if (!crossRepo.putQueueIfApi(crossNo, pro6R22.getProtocol(), "", phaseStageNo, pro6R22)) {
                            Map<String, List<String>> phaseNoListMap = pro6R22.getPhaseNoListMap();
                            List<String> phaseNoList = pro6R22.getPhaseNoList();
                            if (phaseNoListMap != null) {
                                for (List<String> phaseNos : phaseNoListMap.values()) {
                                    for (String phaseNo : phaseNos) {
                                        Pro4S20 pro4S20 = new Pro4S20(slcIp);
                                        pro4S20.setCrossNo(crossNo);
                                        pro4S20.setPhaseNo(phaseNo);
                                        systemRepo.exeSendUdpPro(pro4S20.getCrossNo(), pro4S20.fGetBytes());
//                                        systemComponent.sendCrossMsg(pro4S20.getCrossNo(), pro4S20.fGetBytes());
                                    }
                                }
                            } else if (phaseNoList != null) {
                                for (String phaseNo : phaseNoList) {
                                    Pro4S20 pro4S20 = new Pro4S20(slcIp);
                                    pro4S20.setCrossNo(crossNo);
                                    pro4S20.setPhaseNo(phaseNo);
                                    systemRepo.exeSendUdpPro(pro4S20.getCrossNo(), pro4S20.fGetBytes());
//                                    systemComponent.sendCrossMsg(pro4S20.getCrossNo(), pro4S20.fGetBytes());
                                }
                            }
                            systemRepo.excuteSendThread(pro6R22);
                        }
                        break;
                    case "6R23":
                        //回报相位阶段库中相位晚起时间
                        Pro6R23 pro6R23 = new Pro6R23(slcIp, bTemp);
                        pro6R23.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro6R23.getProtocol(), pro6R23);
                        break;
                    case "6R24":
                        //回报相位阶段库中相位早闭时间
                        Pro6R24 pro6R24 = new Pro6R24(slcIp, bTemp);
                        pro6R24.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro6R24.getProtocol(), pro6R24);
                        break;
                    case "7R10":
                        //回报相位冲突表
                        Pro7R10 pro7R10 = new Pro7R10(slcIp, bTemp);
                        pro7R10.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro7R10.getProtocol(), "", pro7R10.getPhaseNo(), pro7R10);
                        break;
                    case "8R20":
                        //优先
                        Pro8R20 pro8R20 = new Pro8R20(slcIp, bTemp);
                        pro8R20.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro8R20.getProtocol(), pro8R20);
                        break;
                    case "8R50":
                        //紧急
                        Pro8R50 pro8R50 = new Pro8R50(slcIp, bTemp);
                        pro8R50.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro8R50.getProtocol(), pro8R50);
                        break;
                    case "9R20":
                        //回报方案信息
                        Pro9R20 pro9R20 = new Pro9R20(slcIp, bTemp);
                        pro9R20.setCrossNo(crossNo);
                        if (!crossRepo.putQueueIfApi(crossNo, pro9R20.getProtocol(), "", pro9R20.getPlanNo(), pro9R20)) {
                            //获取相阶包含相位
                            List<PhaseStageDetail> phaseStageDetails = pro9R20.getPhaseStageDetails();
                            List<String> stageNoList = new ArrayList<>();
                            phaseStageDetails.forEach(item -> {
                                stageNoList.add(item.getPhaseStageNo());
                            });
                            Pro6S22 pro6S22 = new Pro6S22(slcIp);
                            pro6S22.setCrossNo(pro9R20.getCrossNo());
                            pro6S22.setPhaseStageNoList(stageNoList);
                            systemRepo.exeSendUdpPro(pro9R20.getCrossNo(), pro6S22.fGetBytes());
//                                systemComponent.sendCrossMsg(pro9R20.getCrossNo(), pro6S22.fGetBytes());
                            systemRepo.excuteSendThread(pro9R20);
                        }
                        break;
                    case "9R23":
                        //回报日计划配置
                        Pro9R23 pro9R23 = new Pro9R23(slcIp, bTemp);
                        pro9R23.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro9R23.getProtocol(), pro9R23);
                        break;
                    case "9R28":
                        //回报方案相位阶段出现类型链
                        Pro9R28 pro9R28 = new Pro9R28(slcIp, bTemp);
                        pro9R28.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro9R28.getProtocol(), pro9R28);
                        break;

                    case "11R20":
                        //回报日计划配置
                        Pro11R20 pro11R20 = new Pro11R20(slcIp, bTemp);
                        pro11R20.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro11R20.getProtocol(), "", pro11R20.getDayPlanNo(), pro11R20);
                        break;
                    case "11R21":
                        //回报日计划配置
                        Pro11R21 pro11R21 = new Pro11R21(slcIp, bTemp);
                        pro11R21.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro11R21.getProtocol(), pro11R21);
                        break;
                    case "11R24":
                        //回报日计划配置
                        Pro11R24 pro11R24 = new Pro11R24(slcIp, bTemp);
                        pro11R24.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro11R24.getProtocol(), pro11R24);
                        break;
                    case "12R10":
                        //回报调度表最大使用号
                        Pro12R10 pro12R10 = new Pro12R10(slcIp, bTemp);
                        pro12R10.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro12R10.getProtocol(), pro12R10);
                        break;
                    case "12R20":
                        //回报调度表配置
                        Pro12R20 pro12R20 = new Pro12R20(slcIp, bTemp);
                        pro12R20.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro12R20.getProtocol(), pro12R20.getCrossIdNo(), pro12R20.getScheduleNo(), pro12R20);
                        break;
                    case "12R25":
                        //回报所有调度表编号对应的月份值
                        Pro12R25 pro12R25 = new Pro12R25(slcIp, bTemp);
                        pro12R25.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro12R25.getProtocol(), pro12R25);
                        break;
                    case "12R27":
                        //回报所有调度表编号对应的日计划编号
                        Pro12R27 pro12R27 = new Pro12R27(slcIp, bTemp);
                        pro12R27.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro12R27.getProtocol(), pro12R27);
                        break;

                    case "13R20":
                        //控制模式
                        //回报当前运行状态表
                        Pro13R20 pro13R20 = new Pro13R20(slcIp, bTemp);
                        pro13R20.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro13R20.getProtocol(), pro13R20.getCrossIdNo(), pro13R20);
                        systemRepo.excuteSendThread(pro13R20);
                        break;
                    case "13R23":
                        //回报当前运行方案
                        Pro13R23 pro13R23 = new Pro13R23(slcIp, bTemp);
                        pro13R23.setCrossNo(crossNo);
                        if (!crossRepo.putQueueIfApi(crossNo, pro13R23.getProtocol(), pro13R23.getCrossIdNo(), pro13R23)) {
                            if (crossNo != null) {

                                redisUtil.setCrossCurPlanNo(crossNo, pro13R23.getSlcPlanNo());

                                String slcPlanNo = pro13R23.getSlcPlanNo();
                                Pro9S20 pro9S20 = new Pro9S20(slcIp);
                                pro9S20.setCrossNo(crossNo);
                                pro9S20.setCrossIdNo(pro13R23.getCrossIdNo());
                                pro9S20.setPlanNo(slcPlanNo);
                                systemRepo.exeSendUdpPro(crossNo, pro9S20.fGetBytes());
//                                systemComponent.sendCrossMsg(crossNo, pro9S20.fGetBytes());

                                Pro141S10 pro141S10 = new Pro141S10(slcIp);
                                pro141S10.setCrossNo(crossNo);
                                pro141S10.setPlanNo(slcPlanNo);
                                systemRepo.exeSendUdpPro(crossNo, pro141S10.fGetBytes());
//                                systemComponent.sendCrossMsg(crossNo, pro141S10.fGetBytes());
                            }
                            systemRepo.excuteSendThread(pro13R23);
                        }
                        break;
                    case "13R111":
                        //回报信号机本地时间
                        Pro13R111 pro13R111 = new Pro13R111(slcIp, bTemp);
                        pro13R111.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro13R111.getProtocol(), pro13R111);
                        break;
                    case "130R20":
                        //禁止现场手动状态
                        //回报是否允许现场手动状态
                        Pro130R20 pro130R20 = new Pro130R20(slcIp, bTemp);
                        pro130R20.setCrossNo(crossNo);
                        crossRepo.putQueueIfApi(crossNo, pro130R20.getProtocol(), pro130R20);
                        systemRepo.excuteSendThread(pro130R20);

                        break;
                        case "130R40":
                            //瓶颈控制参数
                            Pro130R40 pro130R40 = new Pro130R40(slcIp, bTemp);
                            pro130R40.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro130R40.getProtocol(), pro130R40);
                            break;
                        case "130R50":
                            //故障配置
                            Pro130R50 pro130R50 = new Pro130R50(slcIp, bTemp);
                            pro130R50.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro130R50.getProtocol(), pro130R50);
                            break;


                        case "134R10":
                            //倒计时配置
                            Pro134R10 pro134R10 = new Pro134R10(slcIp, bTemp);
                            pro134R10.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro134R10.getProtocol(), pro134R10);
                            break;
                        case "134R20":
                            //倒计时配置
                            Pro134R20 pro134R20 = new Pro134R20(slcIp, bTemp);
                            pro134R20.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro134R20.getProtocol(), pro134R20);
                            break;
                        case "134R30":
                            //倒计时配置
                            Pro134R30 pro134R30 = new Pro134R30(slcIp, bTemp);
                            pro134R30.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro134R30.getProtocol(), pro134R30);
                            break;
                        case "134R40":
                            //倒计时配置
                            Pro134R40 pro134R40 = new Pro134R40(slcIp, bTemp);
                            pro134R40.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro134R40.getProtocol(), pro134R40);
                            break;
                        case "134R50":
                            //倒计时配置
                            Pro134R50 pro134R50 = new Pro134R50(slcIp, bTemp);
                            pro134R50.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro134R50.getProtocol(), pro134R50);
                            break;
                        case "134R60":
                            //倒计时配置
                            Pro134R60 pro134R60 = new Pro134R60(slcIp, bTemp);
                            pro134R60.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro134R60.getProtocol(), pro134R60);
                            break;
                        case "134R70":
                            //倒计时配置
                            Pro134R70 pro134R70 = new Pro134R70(slcIp, bTemp);
                            pro134R70.setCrossNo(crossNo);
//                        pro134R70.setCrossNoList(crossNoList);
                            crossRepo.putQueueIfApi(crossNo, pro134R70.getProtocol(), pro134R70);
                            break;

                        case "137R20":

                            Pro137R20 pro137R20 = new Pro137R20(slcIp, bTemp);
                            pro137R20.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro137R20.getProtocol(), pro137R20);
                            break;
                        case "138R10":
                            //倒计时配置
                            Pro138R10 pro138R10 = new Pro138R10(slcIp, bTemp);
                            pro138R10.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro138R10.getProtocol(), pro138R10);
                            break;
                        case "131R45":
                            //倒计时配置
                            Pro131R45 pro131R45 = new Pro131R45(slcIp, bTemp);
                            pro131R45.setCrossNo(crossNo);
                            crossRepo.putQueueIfApi(crossNo, pro131R45.getProtocol(), pro131R45);
                            break;

                        case "140R10":
                            //灯态
                            break;
                        case "140R20":
                            //控制策略
                            Pro140U20 pro140U20 = new Pro140U20(slcIp, bTemp);
                            pro140U20.setCrossNo(crossNo);
                            pro140U20.setProtocol("140R20");
                            crossRepo.putQueueIfApi(crossNo, pro140U20.getProtocol(), pro140U20.getCrossIdNo(), pro140U20);
                            systemRepo.excuteSendThread(pro140U20);
                        case "141R10":
                            //方案特殊输出查询返回
                            Pro141R10 pro141R10 = new Pro141R10(slcIp, bTemp);
                            pro141R10.setCrossNo(crossNo);
                            if (!crossRepo.putQueueIfApi(crossNo, pro141R10.getProtocol(), pro141R10)) {
                                systemRepo.excuteSendThread(pro141R10);
                            }
                            break;
                        case "144R10":
                            //方案特殊输出查询返回
                            Pro144R10 pro144R10 = new Pro144R10(slcIp, bTemp);
                            pro144R10.setCrossNo(crossNo);
                            if (!crossRepo.putQueueIfApi(crossNo, pro144R10.getProtocol(), pro144R10)) {
                                systemRepo.excuteSendThread(pro144R10);
                            }
                            break;
                        case "145R10":
                            //日方案特殊控制回报
                            Pro145R10 pro145R10 = new Pro145R10(slcIp, bTemp);
                            pro145R10.setCrossNo(crossNo);
                            if (!crossRepo.putQueueIfApi(crossNo, pro145R10.getProtocol(), pro145R10)) {
                                systemRepo.excuteSendThread(pro145R10);
                            }
                            break;
                        case "146R10":
                            //排队反溢参数回报
                            Pro146R10 pro146R10 = new Pro146R10(slcIp, bTemp);
                            pro146R10.setCrossNo(crossNo);
                            if (!crossRepo.putQueueIfApi(crossNo, pro146R10.getProtocol(), pro146R10)) {
                                systemRepo.excuteSendThread(pro146R10);
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                    case "40":
                        log.info("接收到设置成功消息： " + slcIp + ",  协议格式：" + protocol + "，协议内容：" + CommonUtil.byte2String(bTemp));
                        //设置正常
                        Pro0D8X pro0D80 = new Pro0D8X(slcIp, protocol, bTemp);
                        pro0D80.setResult(true);
                        crossRepo.putQueueIfApi(crossNo, pro0D80.getReProtocol(), pro0D80);
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
                        crossRepo.putQueueIfApi(crossNo, pro0D81.getReProtocol(), pro0D81);
                        break;
                    case "60":
                        //主动上报
                        switch (protocol) {
                            case "15U20":
                                //告警
                                Pro15U20 pro15U20 = new Pro15U20(slcIp, bTemp);
                                pro15U20.setCrossNo(crossNo);
//                        pro15U20.setCrossNoList(crossNoList);
                                systemRepo.excuteSendThread(pro15U20);
                                break;
                            case "16U20":
                                //故障
                                Pro16U20 pro16U20 = new Pro16U20(slcIp, bTemp);
                                pro16U20.setCrossNo(crossNo);
//                        pro16U20.setCrossNoList(crossNoList);
                                systemRepo.excuteSendThread(pro16U20);
                                break;


                            case "140U10":
                                //灯态
                                Pro140U10 pro140U10 = new Pro140U10(slcIp, bTemp);
                                pro140U10.setCrossNo(crossNo);
                                systemRepo.excuteSendThread(pro140U10);
                                break;

                            case "140U20":
                                //控制策略
                                Pro140U20 pro140U20 = new Pro140U20(slcIp, bTemp);
                                pro140U20.setCrossNo(crossNo);
//                        setCrossNo(pro140U20, crossNoList);
                                ControlState controlState = redisUtil.getControlState(pro140U20.getCrossNo());
                                if (pro140U20.getControlState().equals(controlState)) return;
                                systemRepo.excuteSendThread(pro140U20);
//                        System.out.println("收到140U30：");
                                String slcOrder = redisUtil.getSlcOrder(crossNo);
                                //控制模式
                                Pro13S20 pro13S20 = new Pro13S20(slcIp);
                                pro13S20.setCrossNo(crossNo);
                                pro13S20.setCrossIdNo(slcOrder);
                                systemComponent.sendCrossMsg(crossNo, pro13S20.fGetBytes());
                                break;

                            case "5U71":
                                //920协议检测器数据上报
                                if ((serviceConfig.getDetTypeGb() != 0)) {
                                    Pro5U71 pro5U71 = new Pro5U71(slcIp, bTemp);
                                    pro5U71.setCrossNo(crossNo);
                                    systemRepo.excuteSendDetectThread(pro5U71);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
        }


        private byte[] splitReceiveData (ByteBuf byteBuf){

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

        private byte[] subBytes ( byte[] src, int begin, int count){
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
        private byte[] combineListBytes ( byte[] bytes1, byte[] bytes2){
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

    }
