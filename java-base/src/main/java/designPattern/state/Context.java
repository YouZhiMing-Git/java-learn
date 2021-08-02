package designPattern.state;

/**
 * 上下文类
 * 维护一个state变量，记录当先的状态
 */
public class Context {
    State state;

    public Context(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void execute(){
        state.handle(this);
    }
}
