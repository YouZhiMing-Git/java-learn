package stream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestFileOutputStream {
    public static void main(String[] args) {
        FileInputStream fileInputStream=null;
        FileOutputStream fileOutputStream=null;
        int b=0;
        try {
            fileInputStream=new FileInputStream("src\\main\\java\\stream\\example.txt");
            fileOutputStream=new FileOutputStream("src\\main\\java\\stream\\example_output.txt");
        } catch (FileNotFoundException e) {
            System.out.println("No found file");
            System.exit(-1);
        }
        try{
            while ((b=fileInputStream.read())!=-1){
                //每次读取一个字节，并写进一个字节
                fileOutputStream.write(b);
            }
        }catch (IOException e){
            System.out.println("文件复制错误");
            System.exit(-1);
        }

    }
}
