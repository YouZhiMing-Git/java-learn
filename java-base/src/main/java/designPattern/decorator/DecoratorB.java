package designPattern.decorator;

public class DecoratorB extends Decorator{

    @Override
    public void operation() {
        operationB();
        super.operation();
    }
    public void operationB(){
        System.out.println("穿裤子");
    }
}
