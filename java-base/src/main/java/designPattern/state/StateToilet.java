package designPattern.state;

public class StateToilet implements State{
    @Override
    public void handle(Context context) {
        System.out.println("上厕所。。。。。");
    }
}
