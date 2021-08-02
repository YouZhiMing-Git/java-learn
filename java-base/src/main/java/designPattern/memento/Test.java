package designPattern.memento;

import com.sun.org.apache.xpath.internal.operations.Or;

public class Test {

    public static void main(String[] args) {
        Originator originator=new Originator();
        originator.setState("hello");//设置初始值
        originator.show();

        Caretaker caretaker=new Caretaker();
        caretaker.setMemento(originator.createMemento());//创建备份

        originator.setState("Hi");
        originator.show(); //修改源数据

        originator.setMemento(caretaker.getMemento());//恢复数据
        originator.show();

    }

}
