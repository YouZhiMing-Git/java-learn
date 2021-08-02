package thread.sourcecode;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport 可以理解为 java对线程阻塞和解除阻塞的底层支持
 */
public class T05_LockSupport {

    Object o=new Object();
    public void m1(){

        System.out.println("Hello,this is thread_1");
       /* try {
            for(int i=10;i>0;i--){
                System.out.print(i+" ");
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        /*System.out.println();*/
        System.out.println("park");
        LockSupport.park(o);
      /*  System.out.println("second park");
        LockSupport.park(o);*/
        System.out.println("Bye....");


    }
    public void m2(){

        System.out.println("Hello ,this is thread_2");
        long nanoTime=System.nanoTime();
        LockSupport.parkNanos(o,900000000);
        System.out.println("Bye....");

    }

    public void m3(){
        System.out.println("thread_3 begin ");
        long deadline=System.currentTimeMillis()+10000;
        LockSupport.parkUntil(o,deadline);
        System.out.println("Bye...");
        System.out.println("thread_3 end");
    }
    public void m4(){
        Thread current=Thread.currentThread();
        LockSupport.unpark(current);
        LockSupport.unpark(current);

        System.out.println("1");
        LockSupport.park(o);
        System.out.println(2);
        LockSupport.park(o);
        System.out.println("end");
    }
    public static void main(String[] args) {
        T05_LockSupport s=new T05_LockSupport();
        /*s.m4();*/
        Thread t1=new Thread(()->{
            s.m1();
        },"thread-1");

        t1.start();


       /* System.out.println("unpark");
        LockSupport.unpark(t1);*/
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       t1.interrupt();

      /*Thread t2=  new Thread(()->{
            s.m2();
        },"thread-2");
      t2.start();*/


     /* Thread t3=new Thread(()->{
          s.m3();
      },"thread-3");
      t3.start();*/
    }

    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue<Thread> waiters
            = new ConcurrentLinkedQueue<Thread>();

    public void lock() {
        boolean wasInterrupted = false;
        Thread current = Thread.currentThread();
        waiters.add(current);

        // Block while not first in queue or cannot acquire lock
        while (waiters.peek() != current ||
                !locked.compareAndSet(false, true)) {
            LockSupport.park(this);
            if (Thread.interrupted()) // ignore interrupts while waiting
                wasInterrupted = true;
        }

        waiters.remove();
        if (wasInterrupted)          // reassert interrupt status on exit
            current.interrupt();
    }

    public void unlock() {
        locked.set(false);
        LockSupport.unpark(waiters.peek());
    }
}
