package thread.question;


import java.util.ArrayList;
import java.util.List;

/**
 * 生产者消费者问题
 */
public class ProductorAndCustom<T> {
    List<T> list = new ArrayList<>();
    int MAX = 10;
    int cur = 0;

    public synchronized void put(T t) {
        while (cur == MAX) {
            try {
                //阻塞
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        list.add(t);
        System.out.println(Thread.currentThread().getName() + "生产了一个" + t);
        cur++;
        System.out.println("此时还剩" + cur + t);
        //唤醒消费者线程，也有可能是生产者线程
        this.notifyAll();
    }

    public synchronized T get() {
        T t = null;
        while (cur == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t = list.remove(0);
        cur--;
        System.out.println(Thread.currentThread().getName() + "消费了一个" + t);
        System.out.println("此时还剩" + cur + t);

        //唤醒生产者线程
        this.notifyAll();
        return t;
    }

    public static void main(String[] args) {
        ProductorAndCustom<String> p = new ProductorAndCustom<>();
        //创建生产者线程
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                while (true) {
                    p.put("蛋糕");
                }
            }, "生产者" + i).start();
        }
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                String d = p.get();
            }, "消费者" + i).start();
        }
    }

}
