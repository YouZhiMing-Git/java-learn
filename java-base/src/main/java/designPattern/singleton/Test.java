package designPattern.singleton;

public class Test {
    public static void main(String[] args) {
        LazySingle instance = LazySingle.getInstance();
        LazySingle instance2 = LazySingle.getInstance();
        System.out.println(instance.equals(instance2));

        HungarySingle instance1 = HungarySingle.getInstance();
    }
}
