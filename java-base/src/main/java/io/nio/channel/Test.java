package io.nio.channel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Test {
    public static void main(String[] args) {
        try {
            RandomAccessFile file = new RandomAccessFile("NIO.txt", "rw");//以可读可写的方式拿到文件
          /*  File f=new File("NIO.txt");
            System.out.println(f.length());
            System.out.println(f.exists());*/
            FileChannel channel = file.getChannel();//创建通道对象
            ByteBuffer buffer = ByteBuffer.allocate(64);//创建缓存区，并指定大小
            buffer.clear();

            int bytesRead = channel.read(buffer);//将数据读取进缓存区

            while (bytesRead!=-1){
                System.out.println("Read " + bytesRead);
                buffer.flip();//将缓存区翻转，变为读模式

                while (buffer.hasRemaining()){
                    System.out.println((char) buffer.get());
                }
                buffer.clear();//清空缓存
                bytesRead = channel.read(buffer);//读取
            }
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
