package designPattern.strategy;

public class HalfCount implements Count{
    @Override
    public int getResult(int param) {
        return param>>>1;
    }
}
