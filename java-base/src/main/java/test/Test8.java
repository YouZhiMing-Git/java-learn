package test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author:youzhiming
 * @date: 2024/1/20
 * @description:
 */
public class Test8 {
    public static void main(String[] args) {

        int cycle=66;
        int offset=30;
        LocalDate today = LocalDate.now();
        long todayZone = today.atStartOfDay().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()/1000;
        System.out.println(todayZone);
        long currentTimeMillis = System.currentTimeMillis()/1000;
        System.out.println(currentTimeMillis);


        int l = (int)(currentTimeMillis - todayZone)-offset;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int i = l % cycle;
        long nextStartTime=currentTimeMillis+(cycle-i);
        Instant instant = Instant.ofEpochSecond(nextStartTime);
        String format = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(dateTimeFormatter);
        System.out.println(format);


    }
}
