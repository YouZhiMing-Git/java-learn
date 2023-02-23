package xml.crossphaselampstatus;

import javax.xml.bind.annotation.*;

/**
 * @ClassName:    CrossPhaseLampStatusLamp
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
@XmlType(propOrder = {"phaseNo","lampState"})
@XmlRootElement(name = "PhaseLampStatus")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class CrossPhaseLampStatusLamp {

    private String phaseNo;
    private String lampState;

    @XmlElement(name="PhaseNo")
    public String getPhaseNo() {
        return phaseNo;
    }

    public void setPhaseNo(String phaseNo) {
        this.phaseNo = phaseNo;
    }

    @XmlElement(name="LampStatus")
    public String getLampState() {
        return lampState;
    }

    public void setLampState(String lampState) {
        this.lampState = lampState;
    }
}
