package netty.test.udp.netty;

import com.ehualu.eloc.common.protocolhaixin.busPriority.recv.ProBusPriorityRecv;
import com.ehualu.eloc.common.util.CommonUtil;
import com.ehualu.eloc.sts.system.repository.CrossRepo;
import com.ehualu.eloc.sts.util.LogUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Adam
 * @Date 2021/6/17 10:38
 * @Description Socket通信协议解析类
 */
@Slf4j
@Component
public class UdpBusPriorityProtocol {
    @Autowired
    private CrossRepo crossRepo;


    /*
     * @Author Adam
     * @Description 协议解析
     * @Date 13:47 2021/6/17
     * @Param [slcIp, byteBuf]
     * @return void
     */
    public void transferProtocol(String crossNo, ByteBuf byteBuf) {

        int i = byteBuf.readableBytes();
        byte[] buf=new byte[i];
        byteBuf.readBytes(buf);

        log.info("接收： " + crossNo + "公交优先" + ": " + CommonUtil.byte2String(buf));
        LogUtil.info(crossNo, "公交优先", buf, 2);

        ProBusPriorityRecv proBusPriorityRecv=new ProBusPriorityRecv(crossNo,buf);
        crossRepo.putQueueIfApi(crossNo, proBusPriorityRecv.getBusId(), proBusPriorityRecv);
    }



}
