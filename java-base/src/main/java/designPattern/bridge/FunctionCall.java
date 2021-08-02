package designPattern.bridge;

public class FunctionCall implements Function{
    @Override
    public void execute() {
        System.out.println("call someone");
    }
}
