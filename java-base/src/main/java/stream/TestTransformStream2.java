package stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestTransformStream2 {
    public static void main(String[] args) {
        InputStreamReader inputStreamReader=new InputStreamReader(System.in);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//转换
        String s=null;
        System.out.println("请输入：");
        try {
            s=bufferedReader.readLine();
            while (s!=null){
                if(s.equals("exit"))break;
                System.out.println(s.toUpperCase());
                s=bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
