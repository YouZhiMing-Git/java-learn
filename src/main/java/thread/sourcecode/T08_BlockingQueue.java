package thread.sourcecode;

import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 * 阻塞队列
 * 示例：生产消费问题
 */
public class T08_BlockingQueue {

    public static void main(String[] args) {
        BlockingQueue<Object> queue=new ArrayBlockingQueue<>(10);
        Produce p=new Produce(queue);
        Custom c=new Custom(queue);
        new Thread(p).start();
        new Thread(c).start();


    }
}
class Produce implements Runnable{

    BlockingQueue<Object> queue;
    Produce(BlockingQueue<Object> queue){
        this.queue=queue;
    }
    @Override
    public void run() {
        while (true){
            try {
                this.queue.put(product());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    Object product(){
        return new Object();
    }
}
class Custom implements Runnable{
    BlockingQueue<Object> queue;
    Custom(BlockingQueue<Object> queue){
        this.queue=queue;
    }

    @Override
    public void run() {
        while (true){
            try {
                this.queue.take();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}