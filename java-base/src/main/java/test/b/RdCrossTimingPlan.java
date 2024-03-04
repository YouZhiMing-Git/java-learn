package test.b;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Version: 1.0
 * @Author: szx
 * @ClassName CrossTimingPlan
 * @Deacription TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RdCrossTimingPlan {

    private String crossNo;
    private String planNo;
    private String phaseOrdNo;
    private String controlId;
    private String crossState;
    private String offset;
    private String cycle;
    private List<RdCrossTimingPlanInfo> timingPlanInfo;
    private Long logDate;
}
