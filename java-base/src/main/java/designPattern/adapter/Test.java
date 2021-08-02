package designPattern.adapter;

public class Test {
    public static void main(String[] args) {
       Target target=new Adapter();
        System.out.println(target.getResult());
    }
}
