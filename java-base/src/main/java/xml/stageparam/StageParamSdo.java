package xml.stageparam;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @ClassName:    StageParamSdo
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
//@XmlType(propOrder = {"crossId","stageNo","stageName","attribute","green","redYellow","yellow","allRed","phaseNoList"})
@XmlType(propOrder = {"crossId","stageNo","stageName","attribute","green","phaseNoList"})
@XmlRootElement(name = "StageParam")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class StageParamSdo {

    private String crossId;
    private String stageNo;
    private String stageName;
    private String attribute;
    private String green;
    @XmlTransient
    private String redYellow;
    @XmlTransient
    private String yellow;
    @XmlTransient
    private String allRed;
    private List<String> phaseNoList;

    @XmlElement(name="CrossID")
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    @XmlElement(name="StageNo")
    public String getStageNo() {
        return stageNo;
    }

    public void setStageNo(String stageNo) {
        this.stageNo = stageNo;
    }

    @XmlElement(name="StageName")
    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    @XmlElement(name="Attribute")
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @XmlElement(name="Green")
    public String getGreen() {
        return green;
    }

    public void setGreen(String green) {
        this.green = green;
    }

//    @XmlElement(name="RedYellow")

    public String getRedYellow() {
        return redYellow;
    }

    public void setRedYellow(String redYellow) {
        this.redYellow = redYellow;
    }

//    @XmlElement(name="Yellow")
    public String getYellow() {
        return yellow;
    }

    public void setYellow(String yellow) {
        this.yellow = yellow;
    }

//    @XmlElement(name="AllRed",required = false)
    public String getAllRed() {
        return allRed;
    }

    public void setAllRed(String allRed) {
        this.allRed = allRed;
    }

    @XmlElementWrapper(name="PhaseNoList")
    @XmlElement(name="PhaseNo")
    public List<String> getPhaseNoList() {
        return phaseNoList;
    }

    public void setPhaseNoList(List<String> phaseNoList) {
        this.phaseNoList = phaseNoList;
    }

}