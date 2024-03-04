package test;

import com.alibaba.fastjson.JSON;
import test.c.CrossLampData;

/**
 * @author:youzhiming
 * @date: 2023/9/12
 * @description:
 */
public class Test6 {
    public static void main(String[] args) {
        String str="{\"crossNo\":\"9029\",\"slcType\":\"2\",\"controlMode\":\"C011\",\"controlType\":\"C01\",\"signalCount\":\"17\",\"counterDown\":\"1\",\"counterRestart\":\"1\",\"startValue\":\"0\",\"planId\":\"1\",\"phaseOrder\":\"1\",\"phaseNo\":\"2\",\"stepId\":\"0\",\"stepSec\":\"0\",\"cycleTime\":\"75\",\"offset\":\"0\",\"phaseLampDetail\":[{\"dir\":\"0\",\"laneKind\":\"12\",\"jxid\":\"2\",\"lamp\":\"RED\"},{\"dir\":\"0\",\"laneKind\":\"21\",\"jxid\":\"2\",\"lamp\":\"RED\"},{\"dir\":\"0\",\"laneKind\":\"11\",\"jxid\":\"2\",\"lamp\":\"RED\"},{\"dir\":\"2\",\"laneKind\":\"42\",\"jxid\":\"5\",\"lamp\":\"GREEN\"},{\"dir\":\"2\",\"laneKind\":\"11\",\"jxid\":\"6\",\"lamp\":\"GREEN\"},{\"dir\":\"2\",\"laneKind\":\"13\",\"jxid\":\"6\",\"lamp\":\"GREEN\"}],\"slcId\":\"00000000000111000049\",\"logDate\":\"2023-09-12 17:34:41\"}";
        CrossLampData crossLampData = JSON.parseObject(str, CrossLampData.class);
        System.out.println(JSON.toJSONString(crossLampData));
    }

}
