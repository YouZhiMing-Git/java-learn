package designPattern.state;

public class StateSleep implements State{
    @Override
    public void handle(Context context) {
        System.out.println("睡觉。。。。。");
        context.setState(new StateToilet());
    }
}
