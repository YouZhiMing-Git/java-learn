package xml;

import com.alibaba.fastjson.JSON;
import xml.crossphaselampstatus.CrossPhaseLampStatusMessage;

/**
 * @author:youzhiming
 * @date: 2022/12/1
 * @description:
 */
public class Test2 {
    public static void main(String[] args) {

        String str="{\"crossPhaseLampStatusBody\":{\"crossStageOperation\":{\"crossPhaseLampStatusSdo\":[{\"crossId\":\"1207\",\"crossPhaseLampStatusLamps\":[{\"lampState\":\"21\",\"phaseNo\":\"5\"},{\"lampState\":\"21\",\"phaseNo\":\"25\"},{\"lampState\":\"21\",\"phaseNo\":\"27\"},{\"lampState\":\"21\",\"phaseNo\":\"17\"},{\"lampState\":\"21\",\"phaseNo\":\"23\"},{\"lampState\":\"21\",\"phaseNo\":\"31\"}]}],\"name\":\"Subscribe\",\"order\":\"1\"}},\"from\":{\"address\":{\"instance\":\"\",\"subSys\":\"\",\"sys\":\"UTCS\"}},\"seq\":\"20221201144013000000\",\"to\":{\"address\":{\"instance\":\"\",\"subSys\":\"\",\"sys\":\"TICP\"}},\"token\":\"79d80d70-0bec-4a86-b9d6-cc8424e44f6b\",\"type\":\"PUSH\",\"version\":\"1.0\"}";
        CrossPhaseLampStatusMessage crossPhaseLampStatusMessage = JSON.parseObject(str, CrossPhaseLampStatusMessage.class);
        System.out.println(XmlUtil.bean2Xml(crossPhaseLampStatusMessage));

    }
}
