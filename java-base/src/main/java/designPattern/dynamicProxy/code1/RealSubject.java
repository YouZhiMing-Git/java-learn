package designPattern.dynamicProxy.code1;

public class RealSubject  implements Subject{
    @Override
    public void func1() {
        System.out.println("func1 has been invoke");
    }

    @Override
    public String func2(String var) {
        System.out.println("var is " + var);
        return var+"-------";
    }
}
