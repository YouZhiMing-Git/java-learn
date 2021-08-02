package designPattern.decorator;

/**
 * 对具体装饰者的抽象
 */
public abstract class Decorator implements Component {

    Component component;
    @Override
    public void operation() {
        if (component != null) {
            component.operation();
        }
    }
    public void setComponent(Component component) {
        this.component = component;
    }

}
