package example.io;

import java.lang.reflect.Type;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Buffer 是一个存储特定基础类型数据的容器
 *
 * Buffer 包含三个核心属性：
 * - capacity：Buffer 的容量，在创建 Buffer 的时候指定，存储在 Buffer 中的数据量不能超过 capacity
 * - limit：Buffer 中已经存储的数据容量，limit 永远小于等于 capacity
 * - position：当前游标的位置，可以通过 seek 方法设置 position 的值
 *
 * position <= limit <= capacity
 *
 */
public class BufferMain {

    /**
     * Jdk 内置了基础类型的 Buffer，通过静态方法 allocate 创建 Buffer 实例
     */
    public static Buffer getBuffer(Type type, int size) {

        if (type == Byte.class) {
            return ByteBuffer.allocate(size);
        }
        if (type == Short.TYPE) {
            return ShortBuffer.allocate(size);
        }
        if (type == Integer.class) {
            return IntBuffer.allocate(size);
        }

        return ByteBuffer.allocate(size);

    }


    public Buffer getBuffer(Type type, int size, boolean direct){


        return null;
    }


    public static void initValue(){
        Buffer buffer = getBuffer(Byte.TYPE, 1024);
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        buffer.position(10);
        System.out.println(buffer.position());
    }

    public static void main(String[] args) {
        initValue();
    }

}
