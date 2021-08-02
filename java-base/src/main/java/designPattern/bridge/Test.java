package designPattern.bridge;

public class Test {
    public static void main(String[] args) {
       Product huaWei = new HuaWei();
       huaWei.setFunction(new FunctionCall());
       huaWei.handle();
    }
}
