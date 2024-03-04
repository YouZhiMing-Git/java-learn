package test.b;

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
public class RdCrossTimingPlanPhase {

    private String dir;
    private String laneKind;
    private String lamp;
    private String jxid;

    //晚启动时间
    private String laterStart;
    //早关闭时间
    private String earlyClose;
    //特殊过渡绿闪
    private String excessiveGreenFlash;
    //特殊过渡黄灯
    private String excessiveYellow;
    //特殊过渡全红
    private String excessiveAllRed;
}
