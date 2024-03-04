package netty;

/**
 * @author:youzhiming
 * @date: 2023/10/10
 * @description:
 */
public class App {
    public static void main(String[] args) {
        TcpClient tcpClient=new TcpClient(1, "123", "10.155.30.156", 6652);
        tcpClient.init();

    }
}
