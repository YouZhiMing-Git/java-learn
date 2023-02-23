package xml.crossphaselampstatus;

import javax.xml.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName:    CrossPhaseLampStatusMessage
 * @Author:       szx
 * @Description:
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
@XmlType(propOrder = {"version","token","from","to","type","seq","crossPhaseLampStatusBody"})
@XmlRootElement(name = "Message")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class CrossPhaseLampStatusMessage {

    public void init(){
        this.version = "1.0";
        this.token = "TcpTool.token";
        this.from = "AddressInit.getSendFrom()";
        this.to = "AddressInit.getSendTo();";
        this.type = "";
        SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMddHHmmss");
        this.seq = ft.format(new Date())+"000000";
    }

    private String version;
    private String token;
    private String from;
    private String to;
    private String type;
    private String seq;
    private CrossPhaseLampStatusBody crossPhaseLampStatusBody;

    @XmlElement(name="Version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlElement(name="Token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @XmlElement(name="From")
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @XmlElement(name="To")
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @XmlElement(name="Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name="Seq")
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    @XmlElement(name="Body")

    public CrossPhaseLampStatusBody getCrossPhaseLampStatusBody() {
        return crossPhaseLampStatusBody;
    }

    public void setCrossPhaseLampStatusBody(CrossPhaseLampStatusBody crossPhaseLampStatusBody) {
        this.crossPhaseLampStatusBody = crossPhaseLampStatusBody;
    }
}