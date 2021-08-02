package spring;

import spring.controller.UserController;
import spring.service.UserService;

import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        UserController userController=new UserController();
        Class<? extends UserController> clazz = userController.getClass();
        UserService userService=new UserService();
        //获取所有属性值
        Stream.of(clazz.getDeclaredFields()).forEach(field -> {
            String name=field.getName();
            AutoWired annotation = field.getAnnotation(AutoWired.class);
            if(annotation!=null){
                field.setAccessible(true);
                Class<?> type = field.getType();
                try {
                    Object o = type.newInstance();
                    field.set(userController,o);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }

        });
        System.out.println(userController.getUserService());
    }
}
