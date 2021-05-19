package thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 启动现成的几种方式
 *继承Thread
 * Runnable
 * Callable
 * 线程池 Excutors
 *
 */
public class T02_HowToCreateThread {
    static class  MyThread extends Thread{
        @Override
        public void run() {
            System.out.println("MyThread");
        }
    }

    static class MyRun implements Runnable{
        @Override
        public void run() {
            System.out.println("MyRun");
        }
    }

    static class MyCall implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("MyCall");
            return 1;
        }
    }
    public static void main(String[] args) throws Exception {
        MyThread t=new MyThread();
        t.start();

        new Thread(new MyRun()).start();



        FutureTask<Integer> task=new FutureTask<Integer>(new MyCall());
        new Thread(task).start();

        new Thread(()->{
            System.out.println("lamuda表达式");
        }).start();

    }
}
