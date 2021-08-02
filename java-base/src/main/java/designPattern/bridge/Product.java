package designPattern.bridge;

public abstract class Product {
    Function function;

    abstract void setFunction(Function function);

    void handle() {
        function.execute();
    }
}
