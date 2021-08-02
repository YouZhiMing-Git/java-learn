package designPattern.singleton;

//饿汉式
public class HungarySingle {
    static HungarySingle hungarySingle = new HungarySingle();

    private HungarySingle() {
    }

    public static HungarySingle getInstance() {
        return hungarySingle;
    }

}
