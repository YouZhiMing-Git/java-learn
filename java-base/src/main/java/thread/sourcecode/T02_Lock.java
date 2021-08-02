package thread.sourcecode;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class T02_Lock {
    ReentrantLock rLock=new ReentrantLock();
    int i=0;
    void  testReentrantLock(){
        rLock.tryLock();
        rLock.lock();
        i++;
        rLock.unlock();

    }
    void test1(){
        rLock.lock();
        try {
            System.out.println("thread1 begin....");
            System.out.println("sleep begin...");
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rLock.unlock();
    }
    void test2(){
        System.out.println("thread2 begin ....");
        rLock.lock();
        System.out.println("thread2 getLock ....");
        rLock.unlock();
        System.out.println("thread2 end ....");
    }

     void test3(){
         boolean locked=rLock.tryLock();
        try
        {
            i++;
        }finally {
            if(locked)rLock.unlock();
        }
     }

    Condition c1=rLock.newCondition();
    Condition c2=rLock.newCondition();
    boolean flag=false;
    void testCondition(){

       new Thread(()->{
            rLock.lock();
            try {
                if (!flag){
                    System.out.println(Thread.currentThread().getName()+":我艹，搞不定啊");
                    c2.signal();
                    c1.await();

                }
                System.out.println(Thread.currentThread().getName()+"：劳资又回来了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                rLock.unlock();
            }

       },"thread-1").start();

       new Thread(()->{
           rLock.lock();
           try{
               flag=true;
               System.out.println(Thread.currentThread().getName()+"：去吧，皮卡丘");
               c1.signal();
               c2.await();

           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       },"thread-2").start();
    }

    public static void main(String[] args) {
        T02_Lock t=new T02_Lock();
        /*new Thread(t::testReentrantLock,"thread_0").start();
        new Thread(t::test1,"thread_1").start();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(t::test2,"thread_2").start();*/
        t.testCondition();
    }

}
