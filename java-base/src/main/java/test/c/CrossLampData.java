package test.c;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


/**
 * @author:youzhiming
 * @date: 2023/9/12
 * @description:
 */
@Data
public class CrossLampData {
    @JSONField(ordinal = 1)
    private String crossNo;
    @JSONField(ordinal = 2)
    private String slcType;
    //当前控制模式
    @JSONField(ordinal = 3)
    private String controlMode;
    //之前控制模式
    @JSONField(ordinal = 4)
    private String controlType;
    //时相编号
    @JSONField(ordinal = 5)
    private String signalCount;

    @JSONField(ordinal = 6)
    private String counterDown;
    @JSONField(ordinal = 7)
    private String counterRestart;
    @JSONField(ordinal = 8)
    private String startValue;
    @JSONField(ordinal = 9)

    private String planId;
    @JSONField(ordinal = 10)
    private String phaseOrder;

    //相阶序号
    @JSONField(ordinal = 11)
    private String phaseNo;
    //步阶序号
    @JSONField(ordinal = 12)
    private String stepId;
    //步阶时间
    @JSONField(ordinal = 13)
    private String stepSec;
    @JSONField(ordinal = 14)
    private String controlId;
    @JSONField(ordinal = 15)
    private String cycleTime;
    @JSONField(ordinal = 16)
    private String offset;

    @JSONField(ordinal = 17)
    private String green;
    @JSONField(ordinal = 18)
    private String yellow;
    @JSONField(ordinal = 19)
    private String allRed;
    @JSONField(ordinal = 20)
    private String greenFlash;
    @JSONField(ordinal = 21)
    private String pedFlash;
    @JSONField(ordinal = 22)
    private String minGreen;
    @JSONField(ordinal = 23)
    private String maxGreen;
//    @JSONField(ordinal = 24)
//    private List<MqPhaseLampDetail> phaseLampDetail;

    //车道方向
    @JSONField(ordinal = 24)
    private String dir;
    //车道类型
    @JSONField(ordinal = 25)
    private String laneKind;
    //接线端子
    @JSONField(ordinal = 26)
    private String jxid;
    //灯色
    @JSONField(ordinal = 27)
    private String lamp;

    @JSONField(ordinal = 28)
    private String slcId;
    @JSONField(ordinal = 29)
    private String slcMode;
    @JSONField(ordinal = 30)
    private String logDate;

}
