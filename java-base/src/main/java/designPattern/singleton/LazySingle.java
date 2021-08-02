package designPattern.singleton;

//懒汉式
public class LazySingle {
    private volatile static  LazySingle lazySingle;

    private LazySingle() {
    }

    //双重检查，防止多线程并发情况
    public static LazySingle getInstance() {
        if(lazySingle==null){
            synchronized (LazySingle.class){
                if(lazySingle==null){
                    lazySingle=new LazySingle();
                }
            }
        }
        return lazySingle;
    }
}
