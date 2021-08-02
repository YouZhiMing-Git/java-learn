package designPattern.state;

public class Test {
    public static void main(String[] args) {
        Context context = new Context(new StateEat());

        context.execute();
        context.execute();
        context.execute();
        context.execute();
    }
}
