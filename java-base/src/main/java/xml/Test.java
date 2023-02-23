package xml;

import xml.stageparam.StageParamBody;
import xml.stageparam.StageParamMessage;
import xml.stageparam.StageParamOperation;
import xml.stageparam.StageParamSdo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:youzhiming
 * @date: 2022/11/30
 * @description:
 */
public class Test {
    public static void main(String[] args) {
        StageParamMessage stageParamMessage = new StageParamMessage();
        stageParamMessage.init();
        StageParamBody stageParamBody = new StageParamBody();
        StageParamOperation stageParamOperation = new StageParamOperation();
        List<StageParamSdo> stageParamSdos = new ArrayList<>();

            // 如果相阶编号为空，就返回所有相阶信息


                StageParamSdo stageParamSdo = new StageParamSdo();
                stageParamSdo.setCrossId("1207");
                stageParamSdo.setAllRed("stage.getAllRed()");
                stageParamSdo.setAttribute("stage.getAttribute()");
                stageParamSdo.setGreen("stage.getGreen()");
                stageParamSdo.setRedYellow("stage.getRedYellow()");
                stageParamSdo.setStageName("stage.getStageName()");
                stageParamSdo.setStageNo("stage.getStageNo()");
                stageParamSdo.setYellow("stage.getYellow()");
                stageParamSdo.setPhaseNoList(new ArrayList<>());
                stageParamSdos.add(stageParamSdo);


        stageParamOperation.setStageParamSdo(stageParamSdos);
        stageParamOperation.setOrder("1");
        stageParamOperation.setName("Get");
        stageParamBody.setStageParamOperation(stageParamOperation);
        stageParamMessage.setStageParamBody(stageParamBody);
   /*     stageParamMessage.setSeq(requestMessage.getSeq());
        stageParamMessage.setType(BaseType.RESPONSE);*/
        System.out.println(XmlUtil.bean2Xml(stageParamMessage));
    }
}
