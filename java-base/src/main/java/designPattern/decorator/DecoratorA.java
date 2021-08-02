package designPattern.decorator;

public class DecoratorA extends Decorator{

    @Override
    public void operation() {
        operationA();
        super.operation();
    }
    public void operationA(){
        System.out.println("穿衣服");
    }
}
