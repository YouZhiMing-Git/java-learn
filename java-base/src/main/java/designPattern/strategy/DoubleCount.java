package designPattern.strategy;

public class DoubleCount implements Count{
    @Override
    public int getResult(int param) {
        return param<<1;
    }
}
