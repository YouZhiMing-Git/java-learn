package xml.stageparam;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @ClassName:    StageParamOperation
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
@XmlType(propOrder = {"stageParamSdo"})
@XmlRootElement(name = "Operation")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class StageParamOperation {

    private String order;
    private String name;
    private List<StageParamSdo> stageParamSdo;

    @XmlAttribute(name="order")
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @XmlAttribute(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="StageParam")
    public List<StageParamSdo> getStageParamSdo() {
        return stageParamSdo;
    }

    public void setStageParamSdo(List<StageParamSdo> stageParamSdo) {
        this.stageParamSdo = stageParamSdo;
    }
}