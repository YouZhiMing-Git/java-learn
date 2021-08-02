package designPattern.memento;

/**
 * 管理者类
 * 保存备忘录
 * */
public class Caretaker {
    Memento memento;

    public Memento getMemento() {
        return memento;
    }

    public void setMemento(Memento memento) {
        this.memento = memento;
    }
}
