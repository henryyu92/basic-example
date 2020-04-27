package jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * jvm 参数： -Xmx=100m -Xmn=100m -Xms=256k
 */
public class HeapOOM {

    public static void main(String[] args){
        List<Object> list = new ArrayList<>();
        while(true){
            list.add(new Object());
        }
    }
}
