package stream;

import java.io.*;

/**
 * 转换流
 * 字节流转为字符流
 */
public class TestTransformStream {
    public static void main(String[] args) {
        try {
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(
                    new FileOutputStream("src\\main\\java\\stream\\example_transform.txt"));
            outputStreamWriter.write("鲁迅狂人日记");
            System.out.println(outputStreamWriter.getEncoding());//获取字符编码
            outputStreamWriter.close();
            outputStreamWriter=new OutputStreamWriter(
                    new FileOutputStream("src\\main\\java\\stream\\example_transform.txt",true),"ISO8859_1");//在目标文件后追加内容，而不是覆盖
            outputStreamWriter.write("祥林嫂");
            System.out.println(outputStreamWriter.getEncoding());
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
