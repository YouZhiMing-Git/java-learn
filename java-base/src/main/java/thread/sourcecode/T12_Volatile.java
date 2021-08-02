package thread.sourcecode;

public class T12_Volatile {
       static volatile boolean flag=true;
       static void m1(){
           while (flag){

           }
           System.out.println("end");
       }
       static  void m2(){
           flag=false;
       }

    public static void main(String[] args) throws InterruptedException {
       /* new Thread(()->{
            while (flag){

            }
            System.out.println("end");
        }).start();
        Thread.currentThread().sleep(2000);
        new Thread(()->{
            flag=false;
        }).start();*/
        new Thread(T12_Volatile::m1).start();
        Thread.currentThread().sleep(2000);
        new Thread(T12_Volatile::m2).start();
    }
    /*static volatile boolean  shutdown = false;
    //t1线程
    static void m1(){
        while(!shutdown){
        }
        System.out.println("shutdown...");
    }

    //t2线程
    static void m2(){
        shutdown = true;
        System.out.println("shutdown 更改了");
    }
    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            T12_Volatile.m1();
        },"t1").start();
        //为了让t1充分的运行
        Thread.sleep(1000);
        //t2修改后，t1看不到volatile修改后的值
        new Thread(()->{
            T12_Volatile.m2();
        },"t2").start();

    }*/
}
