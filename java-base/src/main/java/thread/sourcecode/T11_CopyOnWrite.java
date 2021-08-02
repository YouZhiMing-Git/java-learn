package thread.sourcecode;

import java.util.concurrent.CopyOnWriteArrayList;

public class T11_CopyOnWrite {
    public static void main(String[] args) {
        CopyOnWriteArrayList<String> list=new CopyOnWriteArrayList();
        list.add("x");
        list.get(0);
    }
}
