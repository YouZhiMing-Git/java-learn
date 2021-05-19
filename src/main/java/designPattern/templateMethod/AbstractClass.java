package designPattern.templateMethod;

public abstract class AbstractClass {
    //虚拟方法，实现延迟到子类
    protected abstract void method1();
    protected abstract void method2();

    public void execute(){
        method1();
        method2();
    }
}
