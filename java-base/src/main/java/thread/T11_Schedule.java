package thread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author:youzhiming
 * @date: 2024/1/18
 * @description:
 */
public class T11_Schedule {


    private ScheduledExecutorService scheduled = new ScheduledThreadPoolExecutor(1);

    public static void main(String[] args) {

        T11_Schedule t11_schedule=new T11_Schedule();
        t11_schedule.timer(20);

    }

    public void timer(int sec) {
        scheduled.scheduleAtFixedRate(new Runnable() {
            //倒计时秒数
            int secCount = sec;

            @Override
            public void run() {
                System.out.println("倒计时秒：" + secCount);

                if(secCount==10){
                    stopTimer();
                }

                if (secCount == 0) {
                    taskRun();
                    //结束倒计时器
                    stopTimer();
                }
                secCount--;
            }
//        }, 0, 1, TimeUnit.SECONDS);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void stopTimer() {
        scheduled.shutdown();
    }
    private void taskRun(){
        System.out.println("hello world");
    }
}
