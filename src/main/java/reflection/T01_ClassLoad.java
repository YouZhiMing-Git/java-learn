package reflection;

public class T01_ClassLoad {
    public static void main(String[] args) {
        A a=new A();
        System.out.println(A.m);
    }
}
class A{
    static {
        System.out.println("static part init");
        m=300;
    }
    static  int m=100;

    public A(){
        System.out.println("no Param Constructor init ");
    }
}