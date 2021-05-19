package stream;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestFileReadStream {
    public static void main(String[] args) {
        FileReader  fileReader=null;
        int b=0;
        try {
            fileReader=new FileReader("src\\main\\java\\stream\\example_read.txt");
            while ((b=fileReader.read())!=-1){
                System.out.print((char)b);//此时占用两个字节
            }
            fileReader.close();
            System.out.println();
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件");
        } catch (IOException e) {
            System.out.println("文件读取错误");
        }
    }
}
