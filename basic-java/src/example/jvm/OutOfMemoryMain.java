package example.jvm;

import java.util.ArrayList;
import java.util.List;


public class OutOfMemoryMain {

    public static void main(String[] args){
        stackOutOfMemory();
    }

    /**
     *  jvm 参数： -Xmx100m  -Xms100m -Xss256k
     *      java.lang.OutOfMemoryError: Java heap space
     */
    public static void heapOutOfMemory(){
        List<Object> list = new ArrayList<>();
        while(true){
            list.add(new Object());
        }
    }

    /**
     *  jvm 参数： -Xmx100m -Xms100m -Xss256k
     *      java.lang.StackOverflowError
     */
    public static void stackOutOfMemory(){
        stackOutOfMemory();
    }
}
