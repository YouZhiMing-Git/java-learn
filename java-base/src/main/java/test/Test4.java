package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author:youzhiming
 * @date: 2023/8/16
 * @description:
 */
public class Test4 {
    public static void main(String[] args) {
        System.out.println("当前时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        ScheduledExecutorService service=new ScheduledThreadPoolExecutor(20);

        service.schedule(()->{
            System.out.println("当前时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        },5, TimeUnit.SECONDS);
        System.out.println("当前时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
