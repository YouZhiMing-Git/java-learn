package test.b;

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
public class RdCrossTimingPlanInfo {

    private String phaseNo;
    private String phaseSeqNo;
    private String green;
    private String yellow;
    private String allRed;
    private String greenFlash;
    private String personFlash;
    private String minGreen;
    private String maxGreen;
    private String displayFlag;
    private List<RdCrossTimingPlanPhase> phaseInfo;
    //相阶中包含的相位编号集合（4代）
    private List<String> phaseNoList;

}
