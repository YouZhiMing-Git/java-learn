package thread;

/**
 * 线程同步
 */
public class T04_Sync implements Runnable{

    Timer timer=new Timer();

    public static void main(String[] args) {
        T04_Sync sync=new T04_Sync();
        Thread t1=new Thread(sync);
        Thread t2=new Thread(sync);
        t1.setName("T1");
        t2.setName("T2");
        t1.start();
        t2.start();
    }
    @Override
    public void run() {
        timer.add(Thread.currentThread().getName());
    }

}
class Timer {
    private  int num=0;
    //此时锁是当前对象
    public synchronized  void  add(String name){
        num++;
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name+"你是第"+num+"个使用timer的线程");
    }
    //静态方法加锁，此时锁时Timer.class对象
    public synchronized static void coutName(){
        System.out.println(Thread.currentThread().getName());
    }

    //代码块加锁，此时锁是lock对象
    Object lock=new Object();
    public  void coutName1(){
        synchronized (lock){
            System.out.println(Thread.currentThread().getName());
        }
    }
}