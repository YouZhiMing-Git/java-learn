package designPattern.decorator;

/**
 * 主体对象
 */
public class ConcreteComponent implements Component {
    @Override
    public void operation() {
        System.out.println("出门了");
    }
}
