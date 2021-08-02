package thread;

import java.util.concurrent.TimeUnit;

/**
 * sleep:线程睡眠
 * yield:线程退出，让出cpu，回到等待队列
 * join:执行时调用其他线程
 */
public class T03_Sleep_Yield_Join {
    public static void main(String[] args) {
        testJoin();
    }
    static void testSleep() {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);//睡500毫秒，让给别的线程运行
                    // TimeUnit.MICROSECONDS.sleep(500);等价
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static void testYield() {
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("B" + i);
                if (i % 10 == 0)
                    Thread.yield();
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("C" + i);
                if (i % 10 == 0)
                    Thread.yield();
            }
        }).start();
    }


    static void testJoin(){
        Thread t1=new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);//睡500毫秒，让给别的线程运行
                    // TimeUnit.MICROSECONDS.sleep(500);等价
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2=new Thread(()->{
            t1.start();
            System.out.println("T2");
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("T2 end");
        });
        t2.start();
    }
}
