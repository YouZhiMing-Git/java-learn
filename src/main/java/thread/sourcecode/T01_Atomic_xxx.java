package thread.sourcecode;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class T01_Atomic_xxx {
    AtomicInteger atomicInteger = new AtomicInteger(2);

    public void  testMethod(){
        atomicInteger.decrementAndGet();//i--;
        atomicInteger.incrementAndGet();//i++
        atomicInteger.set(5);//i=5;
        atomicInteger.addAndGet(1);//++i
        atomicInteger.getAndAdd(5);//i+5
        atomicInteger.lazySet(8);//i=5

    }



    public void addData() {
        for (int i = 0; i < 5; i++) {
            System.out.println("add data " + atomicInteger.incrementAndGet());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void subData() {
        for (int i = 0; i < 5; i++) {
            System.out.println("sub data" + atomicInteger.decrementAndGet());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     *  var1 当前对象
     *  var2 offset value在对象中的内存地址的偏移量，可理解为地址
     *  var4 value的增加数
     *
     */
   /* public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
    }*/



    AtomicIntegerArray atomicIntegerArray=new AtomicIntegerArray(10);
    public void testArrayMethod(){
        //atomicIntegerArray.addAndGet(2,8);
        System.out.println( atomicIntegerArray.addAndGet(2,8));
    }
    public static void main(String[] args) {
        T01_Atomic_xxx t = new T01_Atomic_xxx();
       /* new Thread(t::addData).start();
        new Thread(t::addData).start();*/
        t.testAtomicReference();
    }

    AtomicReference<User> atomicReference=new AtomicReference<>();
    public void testAtomicReference(){

        atomicReference.set(new User("张三",22));
        User newUser=new User("李四",24);
        User u=atomicReference.getAndSet(newUser);
        System.out.println(u);
    }
}
class User{
    String name;
    int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}