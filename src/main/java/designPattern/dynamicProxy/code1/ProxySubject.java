package designPattern.dynamicProxy.code1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxySubject implements InvocationHandler {
    RealSubject realSubject;

    public ProxySubject(RealSubject realSubject) {
        this.realSubject = realSubject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("begin invoke method: "+method.getName());
        Object result = method.invoke(realSubject, args);
        System.out.println(method.getName()+" is end");
        return result;
    }

    public Object getProxyInstance(){
        return Proxy.newProxyInstance(realSubject.getClass().getClassLoader(),realSubject.getClass().getInterfaces(),this);
    }
}
