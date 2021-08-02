package io.bio.multiThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Handler implements Runnable{
    Socket socket;

    public Handler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream in=null;
        OutputStream out=null;

        try {
            in=socket.getInputStream();
            byte[] bytes = new byte[1024];
            int length=0;
            while ((length=in.read(bytes))>0){
                System.out.println("input is "+new String(bytes,0,length));
                out=socket.getOutputStream();
            }
            out.write("ok".getBytes());
            System.out.println("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
