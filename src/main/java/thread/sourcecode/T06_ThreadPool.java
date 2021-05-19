package thread.sourcecode;

import java.util.concurrent.*;

public class T06_ThreadPool {
    public static void main(String[] args) {

           int COUNT_BITS = Integer.SIZE - 3;
           int CAPACITY   = (1 << COUNT_BITS) - 1;

           int  c=1;
           System.out.println(c & CAPACITY);
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0; i <1; i++) {
            executorService.execute(()->{
                System.out.println(Thread.currentThread().getName() + " is  working");
                try {
                    TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

        }
        executorService.execute(()->{
            System.out.println("third task");
        });
      /*  executorService.shutdown();*/



       /*Future future=executorService.submit(new T());
        try {
            System.out.println(future.get());

        Future future1=executorService.submit(()->{
            System.out.println("Hello");
            return "Hello";
        });
        System.out.println(future1.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        System.out.println("End");
        executorService.shutdown();
    }
    static class T implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            return 10086;
        }
    }
}

