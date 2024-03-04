package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author:youzhiming
 * @date: 2023/3/18
 * @description:
 */
public class Test2 {


    public static void main(String[] args) throws ParseException {
        String beginStr="2023-03-01 00:00";
        String endStr="2023-03-01 01:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long begin = dateFormat.parse(beginStr).getTime();
        long end = dateFormat.parse(endStr).getTime();


        Test2 test2=new Test2();
        test2.dataTrafficTest(begin,end);
    }


    public void dataTrafficTest(long begin, long end) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date currentDate = new Date(begin);
        long currentDataTime = currentDate.getTime();
        String current = dateFormat.format(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        while (currentDataTime<end){

        /*    List<TecData> currentAllDatas = tecDataMapper.selectList(new EntityWrapper<TecData>().like("ReportTime", current));
            List<TecData> clearDatas = dataClearService.clear(currentAllDatas);
            data2EsService.insertData(clearDatas);*/

            //加一分钟
            calendar.add(Calendar.MINUTE,1);
            currentDataTime=calendar.getTimeInMillis();
            current=dateFormat.format(calendar.getTime());
            System.out.println("执行"+current+"数据");

        }

    }
}
