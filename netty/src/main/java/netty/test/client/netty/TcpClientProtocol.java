package netty.test.client.netty;

import com.alibaba.fastjson.JSON;
import com.ehualu.eloc.common.config.ServiceConfig;
import com.ehualu.eloc.common.infrastructure.redis.RedisUtil;
import com.ehualu.eloc.common.protocol.recv.*;
import com.ehualu.eloc.common.util.CommonUtil;
import com.ehualu.eloc.common.util.TimeUtil;
import com.ehualu.eloc.sts.system.SystemComponent;
import com.ehualu.eloc.sts.system.repository.CrossRepo;
import com.ehualu.eloc.sts.system.repository.SystemRepo;
import com.ehualu.eloc.sts.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Version: 1.0
 * @Author: szx
 * @ClassName TcpClientPro
 * @Deacription Socket通信协议解析类
 **/
@Slf4j
@Component
public class TcpClientProtocol {

    @Autowired
    private CrossRepo crossRepo;

    @Autowired
    private SystemComponent systemComponent;

    @Autowired
    private SystemRepo systemRepo;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ServiceConfig serviceConfig;


    /*
     * @Author szx
     * @Description 协议解析函数
     * @Date 14:03 2021/1/18
     * @Param [crossNo, bTemp]
     * @return void
     **/
    public void transferProtocol(String crossNo, byte[] bTemp) {
//        log.info("接收： " + crossNo + "  " + DatatypeConverter.printHexBinary(bTemp));

        redisUtil.setCrossSlcReceiveTime(crossNo, TimeUtil.getNowTimeStampInt());
        if ((bTemp[0] != (byte) 0xAA) || (bTemp[1] == (byte) 0xDD)) return;
        sendAckReplyDataToSlc(crossNo);
        //打印接收日志
        LogUtil.info(crossNo, "", bTemp, 2);
        switch (bTemp[7]) {
            case (byte) 0x0F:
                switch (bTemp[8]) {
                    case (byte) 0x04:
                        Pro0F04 pro0F04 = new Pro0F04(crossNo, bTemp);
                        systemRepo.executeSendThread(pro0F04);
                        if ("1".equals(serviceConfig.getKafakaUsed())) {
                            //连云港告警信息推送
                            log.info("收到要发送的硬件告警kafka消息" + crossNo + CommonUtil.byte2String(bTemp));
                            systemRepo.executeSendCurrentWarringThread(pro0F04);
                        }
                        break;
                    case (byte) 0x80:
                        Pro0F8X pro0F80 = new Pro0F8X(crossNo, bTemp);
                        pro0F80.setResult(true);
                        crossRepo.putQueueIfApi(crossNo, pro0F80.getReProtocol(), pro0F80);
                        if ("5E24".equals(pro0F80.getReProtocol())) {
                            log.info("接收到成功消息： " + crossNo + ",  协议格式：" + pro0F80.getReProtocol() + "，协议内容：" + CommonUtil.byte2String(bTemp));
                        }
//                        System.out.println("接收到成功消息： " + crossNo + ",  协议格式：" + pro0F80.getReProtocol() + "，协议内容：" + CommonUtil.byte2String(bTemp));
                        break;
                    case (byte) 0x81:
                        Pro0F8X pro0F81 = new Pro0F8X(crossNo, bTemp);
                        pro0F81.setResult(false);
                        crossRepo.putQueueIfApi(crossNo, pro0F81.getReProtocol(), pro0F81);
                        log.info("接收到失败消息： " + crossNo + ",  协议格式：" + pro0F81.getReProtocol() + "，协议内容：" + CommonUtil.byte2String(bTemp));

                        if ("5F3F".equals(pro0F81.getReProtocol())) {
                            //初始化登陆失败（依据初始化设置灯态变化推送协议）
                            Integer equipmentId = systemComponent.getClientGroup().get(crossNo).getEquipmentId();
                            Pro0FC2 pro0FC2 = systemComponent.sendAndGetMessage0F42(crossNo, equipmentId + "");
                            systemComponent.getClientGroup().get(crossNo).sendFirstProtocolAndLogIn(pro0FC2);
                        }
                        break;
                    case (byte) 0xC0:
                        Pro0FC0 pro0FC0 = new Pro0FC0(crossNo, bTemp);
                        log.info("收到0FC0：" + JSON.toJSONString(pro0FC0));
                        systemRepo.executeSendThread(pro0FC0);
                        break;
                    case (byte) 0xC2:
                        Pro0FC2 pro0FC2 = new Pro0FC2(crossNo, bTemp);
                        log.info("收到0FC2：" + JSON.toJSONString(pro0FC2));
                        crossRepo.putQueueIfApi(crossNo, pro0FC2.getProtocol(), pro0FC2);
                        break;
                    case (byte) 0x90:
                        Pro0F90 pro0F90 = new Pro0F90(crossNo, bTemp);
                        System.out.println("收到0F90：" + JSON.toJSONString(pro0F90));
                        crossRepo.putQueueIfApi(crossNo, pro0F90.getProtocol(), pro0F90);
                        break;
                }
                break;
            case (byte) 0x30:
                switch (bTemp[8]) {
                    case (byte) 0xC0:
                        Pro30C0 pro30C0 = new Pro30C0(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro30C0.getProtocol(), pro30C0);
                        if (pro30C0 != null && pro30C0.getPhaseOrder() != null && !"".equals(pro30C0.getPhaseOrder())) {
                            TcpClient tcpClient = systemComponent.getClientGroup().get(pro30C0.getCrossNo());
                            if (tcpClient != null) {
                                tcpClient.sendSlcCurrentPhsOrdPlan(pro30C0.getPhaseOrder());
                            }
                        }
                        systemRepo.executeSendThread(pro30C0);
                        break;
                    case (byte) 0xC2:
                        Pro30C2 pro30C2 = new Pro30C2(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro30C2.getProtocol(), pro30C2);
                        if ("1".equals(serviceConfig.getUse30C2Thread())) {
                            systemRepo.executeSend30C2(pro30C2, systemComponent.getCrossNoList().indexOf(crossNo));
                        } else {
                            systemRepo.executeSendThread(pro30C2);
                        }
                        break;
                    case (byte) 0xCD:
                        Pro30CD pro30CD = new Pro30CD(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro30CD.getProtocol(), pro30CD);
                        break;
                    case (byte) 0xE6:
                        Pro30E6 pro30E6 = new Pro30E6(crossNo, bTemp);
                        systemRepo.executeSendThread(pro30E6);
                        break;
                    case (byte) 0xF6:
                        Pro30F6 pro30F6 = new Pro30F6(crossNo, bTemp);
                        systemRepo.executeSendThread(pro30F6);
                        break;
                }
                break;
            case (byte) 0x31:
                switch (bTemp[8]) {
                    case (byte) 0x03:
                        // 2,4和6走920协议
                        if ((serviceConfig.getDetType() == 2) || (serviceConfig.getDetType() == 4) || (serviceConfig.getDetType() == 6)) {
                            Pro3103 pro3103 = new Pro3103(crossNo, bTemp);
                            if (pro3103 != null) {
                                //根据数据类型，组合不同feign接口，相应在STDP中增加对应接口
                                if ("0".equals(pro3103.getParam())) {
                                    //交通数据上报
                                    systemRepo.executeSendDetectThread(pro3103);

                                } else if ("1".equals(pro3103.getParam())) {
                                    //1分钟非机动车流量-修改为检测器数据当中的类型，82F6
                                    pro3103.setProtocol("82F6");
                                    systemRepo.executeSendNewAddDetectThread(pro3103);

                                } else if ("2".equals(pro3103.getParam())) {
                                    //1分钟行人流量-修改为检测器数据当中的类型，82F8
                                    pro3103.setProtocol("82F8");
                                    systemRepo.executeSendNewAddDetectThread(pro3103);

                                } else if ("3".equals(pro3103.getParam())) {
                                    //3==排队长度应答-修改为检测器数据当中的类型，83FE
                                    pro3103.setProtocol("83FE");
                                    systemRepo.executeSendNewAddDetectThread(pro3103);
                                }
                            }
                        }
                        break;
                    case (byte) 0x07:
                        // 环境状态告警上报
                        Pro3107 pro3107 = new Pro3107(crossNo, bTemp);
                        systemRepo.executeSendThread(pro3107);
                        if ("1".equals(serviceConfig.getKafakaUsed())) {
                            //连云港告警信息推送
                            log.info("收到要发送的环境告警kafka消息" + crossNo + CommonUtil.byte2String(bTemp));
                            systemRepo.executeSendCurrentWarringThread(pro3107);
                        }
                        break;
                    case (byte) 0x14:
                        // 查询回报各个监测模块安装状态
                        Pro3114 pro3114 = new Pro3114(crossNo, bTemp);
                        systemRepo.executeSendThread(pro3114);
                        break;
                    case (byte) 0x16:
                        // 设置电子锁开锁回报
                        Pro3116 pro3116 = new Pro3116(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro3116.getProtocol(), pro3116);
                        break;
                    case (byte) 0x2B:
                        //获取三代机核心板类型
//                        Pro312B pro312B = new Pro312B(crossNo,bTemp);
//                        systemRepo.executeSendThread(pro312B);
                        break;
                }
                break;
            case (byte) 0x40:
                switch (bTemp[8]) {
                    case (byte) 0x0F:
                        // 1,3和5走5min协议
                        if ((serviceConfig.getDetType() == 1) || (serviceConfig.getDetType() == 3) || (serviceConfig.getDetType() == 5)) {
                            Pro400F pro400F = new Pro400F(crossNo, bTemp);
                            systemRepo.executeSendDetectThread(pro400F);
                        }
                        break;
                    case (byte) 0xC8:
                        // 查询回报感应通道功能组态资料
                        Pro40C8 pro40C8 = new Pro40C8(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro40C8.getProtocol(), pro40C8);
                        break;
                    case (byte) 0xE0:
                        Pro40E0 pro40E0 = new Pro40E0(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro40E0.getProtocol(), pro40E0);
                        break;
                }
                break;
            case (byte) 0x5E:
                break;
            case (byte) 0x5F:
                switch (bTemp[8]) {
                    case (byte) 0x03:
                        Pro5F03 pro5F03 = new Pro5F03(crossNo, bTemp);
                        systemRepo.executeSendThread(pro5F03);
                        //智慧路口灯态推送-添加灯态来源
                        if (serviceConfig != null && "1".equals(serviceConfig.getSmartIntersection())
                        && "2".equals(serviceConfig.getSmartLampSource())) {
                            systemRepo.executeSendIntelligenceThread(pro5F03);
                        }
                        break;
                    case (byte) 0x08:
                        //主动回报现场设备操作通知
                        Pro5F08 pro5F08 = new Pro5F08(crossNo, bTemp);
                        systemRepo.executeSendThread(pro5F08);
                        break;
                    case (byte) 0x0A:
                        //信号机参数修改日志
                        Pro5F0A pro5F0A = new Pro5F0A(crossNo, bTemp);
                        systemRepo.executeSendThread(pro5F0A);
                        break;
                    case (byte) 0xC0:
                        // 查询回报现行控制策略设定内容
                        Pro5FC0 pro5FC0 = new Pro5FC0(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FC0.getProtocol(), pro5FC0);
                        systemRepo.executeSendThread(pro5FC0);
                        break;
                    case (byte) 0xC3:
                        // 查询回报相序方案内容
                        Pro5FC3 pro5FC3 = new Pro5FC3(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FC3.getProtocol(), pro5FC3.getPhaseOrder(), pro5FC3);
                        systemRepo.executeSendThread(pro5FC3);
                        break;
                    case (byte) 0xC4:
                        // 查询回报配时方案内容
                        Pro5FC4 pro5FC4 = new Pro5FC4(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FC4.getProtocol(), pro5FC4.getPlanId(), pro5FC4);
                        break;
                    case (byte) 0xC5:
                        // 查询回报配时方案内容
                        Pro5FC5 pro5FC5 = new Pro5FC5(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FC5.getProtocol(), pro5FC5.getPlanId(), pro5FC5);
                        break;
                    case (byte) 0xC6:
                        // 查询回报日方案内容
                        Pro5FC6 pro5FC6 = new Pro5FC6(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FC6.getProtocol(), pro5FC6.getSegmentType(), pro5FC6);
                        break;
                    case (byte) 0xC7:
                        // 查询回报现行控制策略设定内容
                        Pro5FC7 pro5FC7 = new Pro5FC7(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FC7.getProtocol(), pro5FC7);
                        break;
                    case (byte) 0xC9:
                        // 查询回报紧急控制内容
                        Pro5FC9 pro5FC9 = new Pro5FC9(crossNo, bTemp);
                        if (!crossRepo.putQueueIfApi(crossNo, pro5FC9.getProtocol(), pro5FC9)) {
                            systemRepo.executeSendThread(pro5FC9);
                        }
                        break;
                    case (byte) 0xCB:
                        // 查询回报现行控制策略设定内容
                        Pro5FCB pro5FCB = new Pro5FCB(crossNo, bTemp);
//                        System.out.println("收到CB："+pro5FCB.toString());
                        crossRepo.putQueueIfApi(crossNo, pro5FCB.getProtocol(), pro5FCB);
                        break;
                    case (byte) 0xCD:
                        // 查询回报现行控制策略设定内容
                        Pro5FCD pro5FCD = new Pro5FCD(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FCD.getProtocol(), pro5FCD);
                        break;
                    case (byte) 0xE3:
                        System.out.println("大小：" + bTemp.length);
                        if (bTemp.length < 140) {
                            redisUtil.setGreenConflictType(crossNo, "1");
                            Pro5FE3Old pro5FE3 = new Pro5FE3Old(crossNo, bTemp);
                            crossRepo.putQueueIfApi(crossNo, pro5FE3.getProtocol(), pro5FE3);
                        } else {
                            redisUtil.setGreenConflictType(crossNo, "0");
                            Pro5FE3 pro5FE3 = new Pro5FE3(crossNo, bTemp);
                            crossRepo.putQueueIfApi(crossNo, pro5FE3.getProtocol(), pro5FE3);
                        }
                        break;
                    case (byte) 0xE5:
                        Pro5FE5 pro5FE5 = new Pro5FE5(crossNo, bTemp);
                        crossRepo.putQueueIfApi(crossNo, pro5FE5.getProtocol(), pro5FE5);
                        break;
                    case (byte) 0xAD:
                        //智慧路口，信号机代传TEC数据上报
                        Pro5FAD pro5FAD = new Pro5FAD(crossNo, bTemp);
                        if (serviceConfig != null && "1".equals(serviceConfig.getSmartIntersection())) {
                            systemRepo.executeSendIntelligenceThread(pro5FAD);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case (byte) 0x6F:
                break;
            default:
                break;
        }

    }

    /*
     * @Author szx
     * @Description 发送正确确认码
     * @Date 9:13 2021/4/5
     * @Param [crossNo]
     * @return void
     **/
    public void sendAckReplyDataToSlc(String crossNo) {
        byte[] buff = new byte[8];
        buff[0] = (byte) 0xaa;
        buff[1] = (byte) 0xdd;
        buff[2] = (byte) 0x00;
        buff[3] = (byte) 0x00;
        buff[4] = (byte) 0x10;
        buff[5] = (byte) 0x00;
        buff[6] = (byte) 0x08;
        byte sum = 0;
        for (int i = 0; i < 7; i++) {
            sum ^= buff[i];
        }
        buff[7] = sum;
        systemComponent.sendCrossMsg(crossNo, buff);
    }
}
