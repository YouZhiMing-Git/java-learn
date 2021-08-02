package thread.sourcecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 同步容器
 */
public class T10_SyncContainer {
    Vector<String> vector=new Vector<>();
    public static void main(String[] args) {



        Hashtable<String,String> table=new Hashtable<>();
        table.put("a","World");
        System.out.println(table.get("a"));

        Collections.synchronizedList(new ArrayList<>());
    }

    public void m1(){
        int index=vector.size()-1;
        vector.add(index,"aaa");
    }
}
