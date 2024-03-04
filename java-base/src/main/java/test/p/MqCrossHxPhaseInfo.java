package test.p;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Version: 1.0
 * @Author: szx
 * @ClassName MgCrossTimingPlanPhase
 * @Deacription TODO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqCrossHxPhaseInfo {


    private String laneKind;
    private String lamp;
    private String jxid;
    private String allRed;
    private String yellow;
    private String green;
    private List<MqCrossHxLaneInfo> planLaneInfos;
}
