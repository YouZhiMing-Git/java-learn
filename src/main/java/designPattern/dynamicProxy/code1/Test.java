package designPattern.dynamicProxy.code1;

public class Test {
    public static void main(String[] args) {

        ProxySubject proxySubject = new ProxySubject(new RealSubject());
        Subject proxyInstance =(Subject) proxySubject.getProxyInstance();
        proxyInstance.func1();
        proxyInstance.func2("lllll");
    }
}
