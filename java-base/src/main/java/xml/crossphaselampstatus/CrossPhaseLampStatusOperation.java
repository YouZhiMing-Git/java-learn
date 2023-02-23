package xml.crossphaselampstatus;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @ClassName:    CrossPhaseLampStatusOperation
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
@XmlType(propOrder = {"crossPhaseLampStatusSdo"})
@XmlRootElement(name = "Operation")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class CrossPhaseLampStatusOperation {

    private String order;
    private String name;
    private List<CrossPhaseLampStatusSdo> crossPhaseLampStatusSdo;

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

    @XmlElement(name="CrossPhaseLampStatus")

    public List<CrossPhaseLampStatusSdo> getCrossPhaseLampStatusSdo() {
        return crossPhaseLampStatusSdo;
    }

    public void setCrossPhaseLampStatusSdo(List<CrossPhaseLampStatusSdo> crossPhaseLampStatusSdo) {
        this.crossPhaseLampStatusSdo = crossPhaseLampStatusSdo;
    }
}