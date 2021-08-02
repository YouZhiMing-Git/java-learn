package designPattern.templateMethod;

public class ConcreteClassB extends AbstractClass {
    @Override
    protected void method1() {
        System.out.println("B method1.... ");
    }

    @Override
    protected void method2() {
        System.out.println("B method2.... ");
    }
}
