package stream;

import java.io.*;

public class OutStreamTest {
    public static void main(String[] args) {
        String path="outStreamTest.txt";
        File file=new File(path);
        try {
            byte[] content=new byte[1024];
            OutputStream outputStream=new FileOutputStream(file);
            outputStream.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
