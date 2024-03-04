package netty.test.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version: 1.0
 * @Author: szx
 * @ClassName TcpClientDecoder
 * @Deacription 拆包粘包处理类
 **/
@Slf4j
public class TcpClientDecoder extends ByteToMessageDecoder {

    // 用来临时保留没有处理过的请求报文
    ByteBuf tempMsg = Unpooled.buffer();

    // 解码器
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        // 合并报文
        ByteBuf message = null;
        int tmpMsgSize = tempMsg.readableBytes();
        // 如果暂存有上一次余下的请求报文，则合并
        if (tmpMsgSize > 0) {
            message = Unpooled.buffer();
            message.writeBytes(tempMsg);
            message.writeBytes(in);
        } else {
            message = in;
        }
        List<Integer> sizeList = getBufSizeList(message);
        if(sizeList.size() == 1){
            byte[] request = new byte[sizeList.get(0)];
            message.readBytes(request);
            if(checkFullPro(request)){
                out.add(Unpooled.copiedBuffer(request));
            } else {
                tempMsg.clear();
                tempMsg.writeBytes(request);
            }
            request = null;
        } else {
            for (int i = 0; i < sizeList.size() - 1; i++) {
                byte[] request = new byte[sizeList.get(i)];
                message.readBytes(request);
                // 将拆分后的结果放入out列表中，交由后面的业务逻辑去处理
                out.add(Unpooled.copiedBuffer(request));
            }
            byte[] request = new byte[sizeList.get(sizeList.size() - 1)];
            message.readBytes(request);
            if(checkFullPro(request)){
                out.add(Unpooled.copiedBuffer(request));
            } else {
                tempMsg.clear();
                tempMsg.writeBytes(request);
            }
            request = null;
        }
    }

    
    /*
     * @Author szx
     * @Description 获取每个协议的整体长度
     * @Date 9:36 2020/12/24
     * @Param [message]
     * @return java.util.List<java.lang.Integer>
     **/
    public List<Integer> getBufSizeList(ByteBuf message){
        int size = message.readableBytes();
        byte[] bTemp = new byte[size];
        message.getBytes(message.readerIndex(), bTemp);
        String msgStr = bytesToHex(bTemp);
        msgStr = msgStr.replaceAll("aabb","|aabb")
                .replaceAll("aadd","|aadd").substring(1);
        String[] msgArray = msgStr.split("\\|");
        List<Integer> list = new ArrayList<>();
        for(String msg : msgArray){
            list.add(msg.length()/2);
        }
        return list;
    }


    /*
     * @Author szx
     * @Description byte数组转换为16进制字符串
     * @Date 9:35 2020/12/24
     * @Param [bytes]
     * @return java.lang.String
     **/
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    /*
     * @Author szx
     * @Description 判断协议的完整性
     * @Date 9:40 2020/12/24
     * @Param [bTemp]
     * @return boolean
     **/
    private boolean checkFullPro(byte[] bTemp){
        if(bTemp.length < 8) return false;
        if(bTemp[0] == (byte) 0xAA){
            if(bTemp[1] == (byte) 0xBB){
                if((bTemp[bTemp.length-3] == (byte) 0xAA)&&(bTemp[bTemp.length-2] == (byte) 0xCC)){
                    return true;
                } else {
                    return false;
                }
            } else if(bTemp[1] == (byte) 0xDD){
                if(bTemp.length == 8) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return false;
    }

}
