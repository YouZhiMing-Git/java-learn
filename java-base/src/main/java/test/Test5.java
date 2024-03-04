package test;

import com.alibaba.fastjson.JSON;
import test.p.MqCrossHxPhaseInfo;
import test.p.MqCrossHxTimingPlan;
import test.p.MqCrossHxTimingPlanInfo;

/**
 * @author:youzhiming
 * @date: 2023/8/17
 * @description:
 */
public class Test5 {
    public static void main(String[] args) {
        String a="{\"crossNo\": \"9020\",\"cycle\": \"40\",\"logDate\": 1690505010032,\"offset\": \"0\",\"planNo\": \"2\",\"timingPlanInfo\": [{\"phaseDataList\": [{\"allRed\": \"3\",\"jxid\": \"9\",\"lamp\": \"1\",\"laneInfoList\": [{\"dir\": \"0\",\"jxid\": \"9\",\"laneKind\": \"11\"},{\"dir\": \"0\",\"jxid\": \"9\",\"laneKind\": \"13\"}],\"yellow\": \"3\"},{\"allRed\": \"3\",\"jxid\": \"10\",\"lamp\": \"1\",\"laneInfoList\": [{\"dir\": \"0\",\"jxid\": \"10\",\"laneKind\": \"11\"},{\"dir\": \"0\",\"jxid\": \"10\",\"laneKind\": \"13\"}],\"yellow\": \"3\"}],\"phaseNo\": \"3\",\"phaseSeqNo\": \"1\",\"stageTime\": \"20\"},{\"phaseDataList\": [{\"allRed\": \"3\",\"jxid\": \"13\",\"lamp\": \"1\",\"laneInfoList\": [{\"dir\": \"0\",\"jxid\": \"13\",\"laneKind\": \"11\"},{\"dir\": \"0\",\"jxid\": \"13\",\"laneKind\": \"13\"}],\"yellow\": \"3\"},{\"allRed\": \"3\",\"jxid\": \"14\",\"lamp\": \"1\",\"laneInfoList\": [{\"dir\": \"0\",\"jxid\": \"14\",\"laneKind\": \"11\"},{\"dir\": \"0\",\"jxid\": \"14\",\"laneKind\": \"13\"}],\"yellow\": \"3\"}],\"phaseNo\": \"4\",\"phaseSeqNo\": \"2\",\"stageTime\": \"20\"}]}";

        String str = a
                .replace("phaseDataList", "phaseData")
                .replace("laneInfoList", "planLaneInfos");

        MqCrossHxTimingPlan mqCrossHxTimingPlan = JSON.parseObject(str, MqCrossHxTimingPlan.class);
        for (MqCrossHxTimingPlanInfo info : mqCrossHxTimingPlan.getTimingPlanInfo()) {

            info.setAllRed(info.getPhaseData().get(0).getAllRed());
            info.setYellow(info.getPhaseData().get(0).getYellow());
            info.setGreen(Integer.parseInt(info.getStageTime()) -
                    Integer.parseInt(info.getPhaseData().get(0).getAllRed()) -
                    Integer.parseInt(info.getPhaseData().get(0).getYellow()) +
                    "");
            info.setAllRed(info.getPhaseData().get(0).getAllRed());
        }

        System.out.println(JSON.toJSONString(mqCrossHxTimingPlan));

    }
}
