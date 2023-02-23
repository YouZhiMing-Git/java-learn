package xml.stageparam;

import javax.xml.bind.annotation.*;

/**
 * @ClassName:    StageParamBody
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
@XmlType(propOrder = {"stageParamOperation"})
@XmlRootElement(name = "Body")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class StageParamBody {

    private StageParamOperation stageParamOperation;

    @XmlElement(name="Operation")
    public StageParamOperation getStageParamOperation() {
        return stageParamOperation;
    }

    public void setStageParamOperation(StageParamOperation stageParamOperation) {
        this.stageParamOperation = stageParamOperation;
    }
}