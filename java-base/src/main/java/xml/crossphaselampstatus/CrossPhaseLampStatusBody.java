package xml.crossphaselampstatus;

import javax.xml.bind.annotation.*;

/**
 * @ClassName:    CrossPhaseLampStatusBody
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
@XmlType(propOrder = {"crossStageOperation"})
@XmlRootElement(name = "Body")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class CrossPhaseLampStatusBody {

    private CrossPhaseLampStatusOperation crossStageOperation;

    @XmlElement(name="Operation")

    public CrossPhaseLampStatusOperation getCrossStageOperation() {
        return crossStageOperation;
    }

    public void setCrossStageOperation(CrossPhaseLampStatusOperation crossStageOperation) {
        this.crossStageOperation = crossStageOperation;
    }
}