package example.concurrent.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 */
public class Cache {

    private static final Map<String, Object> map = new HashMap<>();
    private static ReadWriteLock rwl = new ReentrantReadWriteLock();
    private static Lock r = rwl.readLock();
    private static Lock w = rwl.writeLock();

    public static void put(String key, Object data){
        w.lock();
        try{
            map.put(key, data);
        }finally {
            w.unlock();
        }
    }

    public static final Object get(String key){
        r.lock();
        try{
            return map.get(key);
        }finally{
            r.unlock();
        }
    }

    public static final void clear(){
        w.lock();
        try{
            map.clear();
        }finally{
            w.unlock();
        }
    }
}
