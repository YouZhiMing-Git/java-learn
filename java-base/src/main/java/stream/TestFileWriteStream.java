package stream;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TestFileWriteStream {
    public static void main(String[] args) {
        FileWriter fileWriter=null;
        FileReader fileReader=null;
        int b;
        try {
            fileReader=new FileReader("src\\main\\java\\stream\\example_read.txt");
            fileWriter=new FileWriter("src\\main\\java\\stream\\example_write.txt");
            while ((b=fileReader.read())!=-1){
                fileWriter.write(b);
            }
            fileReader.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
