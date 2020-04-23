package container;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2019/9/27
 */
public class HeapOOM {
    public static void main(String[] args){
        List<Object> list = new ArrayList<>();
        while(true){
            list.add(new Object());
        }
    }
}
