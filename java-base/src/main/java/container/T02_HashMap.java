package container;

import java.util.HashMap;

public class T02_HashMap {
    public static void main(String[] args) {
        HashMap hashMap=new HashMap();
        hashMap.put("key","value");
        String s="aa";
        s.hashCode();
        Object key=new Object();
        int h=key.hashCode();
        System.out.println("hashCode:"+Integer.toBinaryString(key.hashCode()));
        System.out.println("   >>>16:"+Integer.toBinaryString(key.hashCode()>>>16));
        System.out.println("       ^:"+Integer.toBinaryString((h = key.hashCode()) ^ (h >>> 16)));

    }

}
