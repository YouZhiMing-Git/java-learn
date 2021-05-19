package thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用ReentrantLock是可以指定锁为公平锁
 */
public class T08_Lock extends  Thread{
    private static Lock lock=new ReentrantLock(true);//将锁指定为公平锁

    @Override
    public void run() {
        for(int i=0;i<5;i++){
            lock.lock();
            System.out.println(Thread.currentThread().getName()+" get lock");
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        T08_Lock l=new T08_Lock();
        Thread t1=new Thread(l);
        Thread t2=new Thread(l);
        t1.start();
        t2.start();
    }
}
