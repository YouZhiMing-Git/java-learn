package designPattern.bridge;


public class FunctionMusic implements Function{
    @Override
    public void execute() {
        System.out.println("play music");
    }
}
