package test;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author:youzhiming
 * @date: 2024/1/20
 * @description:
 */
public class Test9 {
    public static void main(String[] args) {

        int cycle=68;
        int offset=0;
        LocalDate today = LocalDate.now();
        long todayZone = today.atStartOfDay().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000;
        System.out.println(todayZone);


        LocalDateTime start = LocalDateTime.of(2024, 1, 22, 9, 0);
        long startStamp = start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;

        System.out.println(startStamp);
        long currentTimeMillis = System.currentTimeMillis()/1000;
        System.out.println(currentTimeMillis);

        long cur=todayZone+offset;
        while (cur<currentTimeMillis){
            if(cur<startStamp)
            {
                cur+=cycle;
                continue;

            }
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            Instant instant = Instant.ofEpochSecond(cur);
            String format = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(dateTimeFormatter);
            System.out.println(format);
            cur+=cycle;
        }





    }
}
