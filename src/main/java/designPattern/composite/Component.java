package designPattern.composite;

public abstract class Component {
    String name;

    public Component(String name) {
        this.name = name;
    }


    public abstract void display(int depth);

    public abstract void add(Component... component);

    public abstract void remove(Component component);
}
