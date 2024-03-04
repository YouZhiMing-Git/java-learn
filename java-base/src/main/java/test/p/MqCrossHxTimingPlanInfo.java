package test.p;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Version: 1.0
 * @Author: szx
 * @ClassName CrossTimingPlanInfo
 * @Deacription TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqCrossHxTimingPlanInfo {

    private String phaseNo;
    private String phaseSeqNo;
    private String stageTime;
    private String displayFlag;
    private String green;
    private String yellow;
    private String allRed;
    private List<MqCrossHxPhaseInfo> phaseData;


}
