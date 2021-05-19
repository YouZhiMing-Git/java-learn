package designPattern.memento;

/**
 * 备忘录类，保存需要的属性
 * */
public class Memento {
    String state;//需要保存的属性

    public Memento(Originator originator){
        state=originator.state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
