package reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Test {

    public static void main(String[] args) throws Exception {
        Class clazz = null;
        //通过forName获取
        clazz = Class.forName("reflection.Apple");
        //通过类名.class获取
        clazz = Apple.class;
        Apple apple = new Apple();
        //通过对象获取
        clazz = apple.getClass();


        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType() == String.class) {
                System.out.println("aaa");
            }
            System.out.println(declaredField);
        }
        ;

        Constructor constructor = clazz.getConstructor();
        Object o = constructor.newInstance();

        Method setWeight = clazz.getMethod("setWeight", int.class);
        String methodName = setWeight.getName();
        System.out.println(methodName);
        setWeight.invoke(o, 14);
        System.out.println(o);


    }
}
