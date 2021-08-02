package thread;

import java.util.concurrent.TimeUnit;

/**
 * 线程是一个程序的不同的运行路径
 */
public class T01_WhatIsThread {
    static class T extends Thread{
        @Override
        public void run() {
            for(int i=0;i<10;i++){
                try {
                    TimeUnit.MICROSECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("T");
            }

        }
    }

    public static void main(String[] args) {
        T t=new T();
        t.run();//方法调用
//        t.start();//启动线程
        for(int i=0;i<10;i++){
            try {
                TimeUnit.MICROSECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("main()");
    }
}
