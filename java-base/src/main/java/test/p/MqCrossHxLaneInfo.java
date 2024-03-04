package test.p;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class MqCrossHxLaneInfo {

    private String dir;
    private String laneKind;
    private String jxid;

 
}
