package designPattern.memento;

/**
 * 源类
 * 负责生成备份
 *
 * */
public class Originator {
    String state;//类中的状态

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Memento createMemento(){
        return new Memento(this);
    }
    public void setMemento(Memento memento){
        state=memento.state;
    }

    public  void show(){
        System.out.println(state);
    }

}
