package designPattern.state;

public class StateEat implements State{
    @Override
    public void handle(Context context) {
        System.out.println("吃饭。。。。。");
        context.setState(new StateSleep());//规定下一个状态
    }
}
