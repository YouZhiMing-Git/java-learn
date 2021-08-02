package thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class T06_Lock {
    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();
            for (int i = 0; i < 10; i++) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }
    /*使用trylock进行尝试锁定，不管锁定与否，
    方法豆浆继续执行,可以根据trylock的返回值来判定是否锁定,
    也可以指定tryLock的时间，由于trylock（time）抛出异常，
    所以要注意unlock的处理， 必须放到finally中*/
    void m2(){
        boolean locked=lock.tryLock();
        System.out.println("m2 locked");
        if(locked)lock.unlock();
        locked=false;
       try {
           //等待5s后捕获锁，若失败则返回false
           locked=lock.tryLock(5,TimeUnit.SECONDS);
           System.out.println("m2 locked");
       } catch (InterruptedException e) {
           e.printStackTrace();
       }finally {
           if(locked)lock.unlock();
       }

    }

    public static void main(String[] args) {
        T06_Lock t=new T06_Lock();
        new Thread(t::m1).start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(t::m2).start();
    }


}
