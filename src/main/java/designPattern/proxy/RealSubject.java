package designPattern.proxy;

public class RealSubject implements Subject{
    @Override
    public void execute() {
        System.out.println("I am realSubject,I am doing something");
    }
}
