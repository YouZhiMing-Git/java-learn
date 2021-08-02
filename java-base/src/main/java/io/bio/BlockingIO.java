package io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingIO {
    public static void main(String[] args) {
        int port=8080;//定义端口
        ServerSocket serverSocket=null;//创建服务端；
        Socket socket=null;//服务端
        InputStream in=null;
        OutputStream out=null;

        try {
            serverSocket=new ServerSocket(port);//创建Server端，并指定端口

            while (true){
                System.out.println("start");
                socket=serverSocket.accept();//阻塞获取
                in=socket.getInputStream();
                byte[] bytes = new byte[1024];
                int length=0;
                while ((length=in.read(bytes))>0){
                    System.out.println("input is "+new String(bytes,0,length));
                    out=socket.getOutputStream();
                }
                out.write("ok".getBytes());
                System.out.println("end");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }


    }
}
