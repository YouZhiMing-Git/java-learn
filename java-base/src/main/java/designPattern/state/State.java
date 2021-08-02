package designPattern.state;


/***
 * 定义状态接口
 * 定义不同的状态所要执行的方法
 * 所有状态都要实现该接口
 */

public interface State {
    public void handle(Context context);
}
