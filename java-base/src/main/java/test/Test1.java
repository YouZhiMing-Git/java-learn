package test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author:youzhiming
 * @date: 2022/10/21
 * @description:
 */
public class Test1 {
    public static void main(String[] args) {

        Test1 t = new Test1();
        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());


        for (int i = 0; i < 1; i++) {
            executorService.execute(() -> {
                System.out.println("task"  + "execute");
                try {
                    executorService.submit(() -> {
                        t.doSomething();
                    }).get(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public String doSomething() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
