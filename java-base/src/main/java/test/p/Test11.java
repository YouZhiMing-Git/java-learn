package test.p;

import javax.xml.bind.DatatypeConverter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author:youzhiming
 * @date: 2024/3/4
 * @description:
 */
public class Test11 {
    public static void main(String[] args) {
//        byte[] buffer = {
//                (byte) 0x7E, (byte) 0x00, (byte) 0x14, (byte) 0x10, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
//                (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x30, (byte) 0x01, (byte) 0x01, (byte) 0x05,
//                (byte) 0x86, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x4E, (byte) 0xC8, (byte) 0x7D
//        };
//        // 打印 byte 数组内容
//        for (byte b : buffer) {
//            System.out.print(b + " ");
//        }

        String command;
        //0-开启，1-关闭

        command = "7E0013100001000000010100100101048601000050137D";

        try {
            // 创建UDP套接字
            try (DatagramSocket socket = new DatagramSocket()) {
                // 将消息转换为字节数组
                byte[] buffer = DatatypeConverter.parseHexBinary(command);
                // 创建DatagramPacket，指定数据、数据长度、目标IP地址和端口号
                InetAddress address = InetAddress.getByName("10.155.32.155");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 161);
                // 发送数据报
                socket.send(packet);
                System.out.println("发送成功");
                socket.setSoTimeout(50000); // 设置5秒的超时时间

                // 准备接收响应的字节数组
                byte[] receiveBuffer = new byte[1024]; // 根据实际需要设置缓冲区大小
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                try {
                    // 接收数据报
                    socket.receive(receivePacket);
                } catch (Exception e) {
                    System.out.println("出错了" + e.getMessage());
                }

                // 将接收到的字节转换为字符串
                int length = receivePacket.getLength();
                byte[] temp=new byte[length];
                for (int i = 0; i < length; i++) {
                    temp[i]=receivePacket.getData()[i];
                }
                String s = DatatypeConverter.printHexBinary(temp);

                System.out.println("接收数据成功"+s);
//                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
//                System.out.println("Received response: " + response);

                // 这里可以根据需要对响应进行处理
                // 例如，检查响应是否符合预期格式，或者是否包含特定的确认信息等

                // 假设响应只是简单的成功或失败消息
//                if (response.contains("success")) {
//                    System.out.println("接收数据成功");
//                } else {
//                    System.out.println("接收数据失败");
//                }
            } // DatagramSocket 会在这里自动关闭
        } catch (Exception e) {
            System.out.println("出错了");
        }


    }
}
