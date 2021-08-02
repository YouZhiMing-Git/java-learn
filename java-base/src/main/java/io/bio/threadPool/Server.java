package io.bio.threadPool;

import io.bio.multiThread.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int port=8080;//定义端口
        ServerSocket serverSocket=null;//创建服务端；
        Socket socket=null;//服务端
        InputStream in=null;
        OutputStream out=null;

        HandlerExecutePool handlerExecutePool=new HandlerExecutePool(10,100);
        try {
            serverSocket=new ServerSocket(port);//创建Server端，并指定端口

            while (true){
                System.out.println("start");
                socket=serverSocket.accept();//阻塞获取
               handlerExecutePool.execute(new Handler(socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }


    }
}
