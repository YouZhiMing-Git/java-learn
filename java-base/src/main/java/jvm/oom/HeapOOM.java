package jvm.oom;

import java.util.ArrayList;
import java.util.List;


/**
 *  限制java堆大小20M，不可扩展（将对的最小值和最大值设置为一样就可以避免自动扩展,通过参数-XX：+HeapDumpOnOutOf-MemoryError可以让虚拟机 在出现内存溢出异常的时候Dump出当前的内存堆转储快照以便进行事后分析）
 *  虚拟机参数： -Xms20m -Xmx20m -XX: +HeapDumpOnOutOfMemoryError
 */
public class HeapOOM {
    static class OOMObject{

    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();

        while (true){
            list.add(new OOMObject());
        }
    }
}
