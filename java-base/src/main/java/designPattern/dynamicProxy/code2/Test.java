package designPattern.dynamicProxy.code2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {
        //传入目标对象，真实对象
        RealSubject realSubject = new RealSubject();
        Subject subjectProxy=(Subject) getProxy(realSubject);
        subjectProxy.func1();
        System.out.println(subjectProxy.func2("hello"));
    }
    private static Object getProxy(final  Object obj){
        Object proxy = Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(obj, args);
                    }
                });
        return proxy;
    }
}
