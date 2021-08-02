package designPattern.templateMethod;

public class Test {
    public static void main(String[] args) {
        AbstractClass ac;

        ac=new ConcreteClassA();
        ac.execute();

        ac=new ConcreteClassB();
        ac.execute();

    }
}
