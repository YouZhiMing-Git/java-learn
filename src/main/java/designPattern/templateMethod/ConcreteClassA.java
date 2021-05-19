package designPattern.templateMethod;

public class ConcreteClassA extends AbstractClass {
    @Override
    protected void method1() {
        System.out.println("A method1.... ");
    }

    @Override
    protected void method2() {
        System.out.println("A method2.... ");
    }
}
