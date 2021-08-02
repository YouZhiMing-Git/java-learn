package designPattern.adapter;

//适配器
public class Adapter extends Target {
    Adaptee adaptee = new Adaptee();

    @Override
    public int getResult() {
        return adaptee.getSpecificResult();
    }
}
