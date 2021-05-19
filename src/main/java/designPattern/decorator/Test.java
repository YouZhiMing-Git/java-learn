package designPattern.decorator;

public class Test {
    public static void main(String[] args) {
        //具体对象
        Component person=new ConcreteComponent();
        //示例化包装类
        DecoratorA decoratorA = new DecoratorA();
        DecoratorB decoratorB = new DecoratorB();

        decoratorA.setComponent(person);
        decoratorB.setComponent(decoratorA);
        decoratorB.operation();
    }
}
