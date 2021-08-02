package stream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestFileInputStream {
    public static void main(String[] args) {
        int b=0;
        FileInputStream fileInputStream=null;
        try {
            fileInputStream=new FileInputStream("src\\main\\java\\stream\\example.txt");
        } catch (FileNotFoundException e) {
            System.out.println("No found file");
            System.exit(-1);
        }
        try{
            long num=0;//记录读取多少字节
            while ((b=fileInputStream.read())!=-1){
                System.out.print((char)b);
                num++;
            }
            System.out.println();
            System.out.println("共读取了"+num+"字节");
            fileInputStream.close();
        }catch (Exception e){
            System.out.println("文件读取错误");
            System.exit(-1);
        }
    }
}
