package stream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 缓冲流
 * 减少系统实际对原始数据来源的访问次数，一次能对多个数据单位进行操作
 *从原始流（fileInputStream)成块读入或积累到一大块在批量写入
 */
public class TestBufferStream {
    public static void main(String[] args) {
        try {
            FileInputStream fileInputStream=new FileInputStream("src\\main\\java\\stream\\example.txt");
            BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);//对fileInputStream套了一层，对fileInputStream进行加工
            int b=0;
            System.out.println(bufferedInputStream.read());
            System.out.println(bufferedInputStream.read());
            //标记点，
            bufferedInputStream.mark(2);
            for(int i=0;i<10;i++){
                System.out.print((char) bufferedInputStream.read()+" ");
            }
            System.out.println();
            bufferedInputStream.reset();//返回标记点
            for (int i = 0; i <=10&&(b=bufferedInputStream.read())!=-1 ; i++) {
                System.out.print((char)b+"");
            }
            bufferedInputStream.close();//关闭流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
