package stream;

import java.io.*;

/**
 *
 */
public class TestBufferReadStream {
    public static void main(String[] args) {
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader("src\\main\\java\\stream\\example_bufferedReader.txt"));
            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter("src\\main\\java\\stream\\example_bufferedWriter.txt"));
            String s=null;
            for(int i=0;i<100;i++){
                s=String.valueOf(Math.random());//0-1随机数
                bufferedWriter.write(s);
                bufferedWriter.newLine();//换行
            }
            bufferedWriter.flush();
            while ((s=bufferedReader.readLine())!=null){
                System.out.println(s);
            }
            bufferedReader.close();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
