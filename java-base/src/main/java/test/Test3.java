package test;

/**
 * @author:youzhiming
 * @date: 2023/7/28
 * @description:
 */
public class Test3 {
    public static void main(String[] args) {
        byte b=0x00;
        System.out.println(Byte.toString((byte) (0x01 << -1 | b)));
    }
}
