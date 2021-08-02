package thread.sourcecode;

import java.util.concurrent.ConcurrentHashMap;

public class T09_ConcurrentHashMap {
    public static void main(String[] args) {
        ConcurrentHashMap<String,String> map=new ConcurrentHashMap<>();
        new Thread(()->{
            map.put("hello","world");
        }).start();
    }
}
