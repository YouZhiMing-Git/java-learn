package thread.sourcecode;

import javax.sound.midi.Track;
import java.util.Vector;

public class T07_ThreadLocal {

    static ThreadLocal<String> local = new ThreadLocal<>();

    public static void print() {
        local.get();
        System.out.println(Thread.currentThread().getName() + local.get());
    }

    public static void remove() {
        local.remove();
    }

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            local.set("Hello");

            print();
            remove();
            System.out.println("thread-1 after remove");
            print();
        }, "thread-1");
        Thread t2= new Thread(() -> {
            local.set("Bye Bye");

            print();
            remove();
            System.out.println("thread-2 after remove");
            print();
        }, "thread-2");

        t1.start();
//        t2.start();

    }
}
