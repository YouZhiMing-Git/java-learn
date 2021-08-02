package io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class NIOServer {
    public static void main(String[] args) throws Exception{

        LinkedList<SocketChannel> clients=new LinkedList<>();

        ServerSocketChannel ss = ServerSocketChannel.open();//服务端开启监听，接收客户端
        ss.bind(new InetSocketAddress(9090));
        ss.configureBlocking(false);//重点，配置非阻塞 只让接收客户端不阻塞
        while (true){
            //接收客户端连接
            /***
             * accept 调用内核方法 ：
             * 要么有客户端接入进来，返回客户端的fd
             * 要么没有客户端接入
             *      在BIO模式：一直卡着
             *      在NIO模式：不卡着 ，返回-1
             */
            SocketChannel client = ss.accept();//非阻塞方式，要么返回客户端，要么返回null（-1）

            if(client==null){

            }else {
                client.configureBlocking(false);// socket（服务端的listen socket《姐姐请求三次握手后，）
                clients.add(client);
            }

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);//可以在堆里
            int num;
            //遍历所有的客户端能不能读取数据
            for (SocketChannel c : clients) {
                 num = c.read(byteBuffer);// >0 /-1 /0  非阻塞模式
                if(num>0){
                    byteBuffer.flip();//切换缓存模式，从写模式切换到读模式

                    byte[] bytes = new byte[byteBuffer.limit()];//缓存的使用大小
                    byteBuffer.get(bytes); //将缓存区的的数据写入byte数组

                    String b=new String(bytes);
                    System.out.println(c.socket().getPort()+":"+b);
                    byteBuffer.clear();
                }
            }
        }
    }
}
