package designPattern.proxy;

public class Test {
    public static void main(String[] args) {
        ProxySubject proxySubject = new ProxySubject(new RealSubject());
        proxySubject.execute();
    }
}
