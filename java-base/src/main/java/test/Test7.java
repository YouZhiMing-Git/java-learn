package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import test.b.RdCrossTimingPlan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author:youzhiming
 * @date: 2023/10/23
 * @description:
 */
public class Test7 {
    public static void main(String[] args) {
       /* SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取科特当前时间
        df.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        System.out.println(df.format(new Date()));*/
//        System.out.println((-6)%8);

        String s="{\"controlId\":\"0\",\"crossNo\":\"1375\",\"crossState\":\"C011\",\"cycle\":\"100\",\"logDate\":1700445472,\"offset\":\"72\",\"phaseOrdNo\":\"1\",\"planNo\":\"1\",\"timingPlanInfo\":[{\"allRed\":\"5\",\"displayFlag\":\"0\",\"green\":\"52\",\"greenFlash\":\"3\",\"maxGreen\":\"100\",\"minGreen\":\"10\",\"personFlash\":\"5\",\"phaseInfo\":[{\"dir\":\"0\",\"jxid\":\"5\",\"lamp\":\"1\",\"laneKind\":\"21\"},{\"dir\":\"0\",\"jxid\":\"5\",\"lamp\":\"1\",\"laneKind\":\"22\"},{\"dir\":\"6\",\"jxid\":\"25\",\"lamp\":\"1\",\"laneKind\":\"100\"},{\"dir\":\"4\",\"jxid\":\"17\",\"lamp\":\"1\",\"laneKind\":\"21\"},{\"dir\":\"4\",\"jxid\":\"17\",\"lamp\":\"1\",\"laneKind\":\"22\"},{\"dir\":\"2\",\"jxid\":\"29\",\"lamp\":\"1\",\"laneKind\":\"100\"}],\"phaseNo\":\"1\",\"phaseSeqNo\":\"1\",\"yellow\":\"3\"},{\"allRed\":\"3\",\"displayFlag\":\"0\",\"green\":\"34\",\"greenFlash\":\"3\",\"maxGreen\":\"100\",\"minGreen\":\"10\",\"personFlash\":\"5\",\"phaseInfo\":[{\"dir\":\"2\",\"jxid\":\"11\",\"lamp\":\"1\",\"laneKind\":\"12\"},{\"dir\":\"2\",\"jxid\":\"11\",\"lamp\":\"1\",\"laneKind\":\"22\"},{\"dir\":\"0\",\"jxid\":\"27\",\"lamp\":\"1\",\"laneKind\":\"100\"},{\"dir\":\"6\",\"jxid\":\"23\",\"lamp\":\"1\",\"laneKind\":\"12\"},{\"dir\":\"6\",\"jxid\":\"23\",\"lamp\":\"1\",\"laneKind\":\"22\"},{\"dir\":\"4\",\"jxid\":\"31\",\"lamp\":\"1\",\"laneKind\":\"100\"}],\"phaseNo\":\"2\",\"phaseSeqNo\":\"2\",\"yellow\":\"3\"}]}";

        RdCrossTimingPlan rdCrossTimingPlan = JSON.parseObject((String) s, new TypeReference<RdCrossTimingPlan>() {
        });
        System.out.println(JSON.toJSONString(rdCrossTimingPlan));

    }

}
