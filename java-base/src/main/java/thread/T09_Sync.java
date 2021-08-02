package thread;

import org.openjdk.jol.info.ClassLayout;

public class T09_Sync {

    static volatile int i = 0;

    public static void n() { i++; }

    public static synchronized void m() {}

    public static void main(String[] args) {
        for(int j=0; j<1000_000; j++) {
            m();
            n();
        }
    }
   /* public static void main(String[] args) {
        Object o=new Object();
//        System.out.println(ClassLayout.parseInstance(o).toPrintable());

        synchronized (o){
            *//*System.out.println(ClassLayout.parseInstance(o).toPrintable());*//*
            System.out.println("Hello");
        }

    }*/
}
