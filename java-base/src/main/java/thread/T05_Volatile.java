package thread;

import java.util.concurrent.TimeUnit;

public class T05_Volatile {
   volatile boolean running=true;
    void m(){
        System.out.println("m start");
        while (running){

        }
        System.out.println("m end");
    }

    public static void main(String[] args) {
        T05_Volatile t=new T05_Volatile();
        new Thread(t::m,"t1").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.running=false;
    }
}
