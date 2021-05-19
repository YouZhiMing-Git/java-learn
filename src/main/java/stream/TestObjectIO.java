package stream;

import java.io.*;

/**
 * Object流：将对象转换为字节流
 *
 */
public class TestObjectIO {
    public static void main(String[] args) {
        T t=new T();
        t.k=8;
        try {
            //指定输出文件
            FileOutputStream fileOutputStream=new FileOutputStream("src\\main\\java\\stream\\example_object.txt");
            //套一层ObjectOutputStream管道
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(t);
            objectOutputStream.flush();
            objectOutputStream.close();

            //读取信息
            FileInputStream fileInputStream=new FileInputStream("src\\main\\java\\stream\\example_object.txt");

            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            T tRead=(T)objectInputStream.readObject();
            System.out.println(tRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}

/**
 * 若对象要序列化，则必须要实现Serializable接口
 */
class T implements Serializable{
    int i=0;
    int j=9;
    double d=2.3;

    //添加transient关键字，序列化时不做处理
    transient  int k=15;

    @Override
    public String toString() {
        return "T{" +
                "i=" + i +
                ", j=" + j +
                ", d=" + d +
                ", k=" + k +
                '}';
    }
}