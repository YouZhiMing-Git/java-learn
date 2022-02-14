package jvm.tool;


/**
 * @author:youzhiming
 * @date: 2021/10/21
 * @description:
 */
public class JHSDB_TestCase {
    static class Test {
        //放在方法区
        static ObjectHolder staticObj = new ObjectHolder();
        //放在堆
        ObjectHolder instanceObj = new ObjectHolder();
        void foo() {
            //放在foo的局部变量表
            ObjectHolder localObj = new ObjectHolder();
            System.out.println("done");
        }

    }
    private  static class ObjectHolder{}

    public static void main(String[] args) {
        Test test = new Test();
        test.foo();
    }
}
