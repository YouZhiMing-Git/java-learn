package xml.crossphaselampstatus;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @ClassName:    CrossPhaseLampStatusSdo
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
@XmlType(propOrder = {"crossId","crossPhaseLampStatusLamps"})
@XmlRootElement(name = "CrossPhaseLampStatus")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class CrossPhaseLampStatusSdo {

    private String crossId;
    private List<CrossPhaseLampStatusLamp> crossPhaseLampStatusLamps;

    @XmlElement(name="CrossID")
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    @XmlElementWrapper(name="PhaseLampStatusList")
    @XmlElement(name="PhaseLampStatus")
    public List<CrossPhaseLampStatusLamp> getCrossPhaseLampStatusLamps() {
        return crossPhaseLampStatusLamps;
    }

    public void setCrossPhaseLampStatusLamps(List<CrossPhaseLampStatusLamp> crossPhaseLampStatusLamps) {
        this.crossPhaseLampStatusLamps = crossPhaseLampStatusLamps;
    }
}