package designPattern.strategy;

import java.lang.reflect.InvocationTargetException;

public class Test {
    public static void main(String[] args) {
        try {
       //     String classStr="designPattern.strategy.DoubleCount";
            String classStr="designPattern.strategy.HalfCount";
            Class<?> clazz = Class.forName(classStr);
            Count count = (Count) clazz.getConstructor().newInstance();
            CountContent countContent = new CountContent(count);
            System.out.println(countContent.getResult(2));


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
