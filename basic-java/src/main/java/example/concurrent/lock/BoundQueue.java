package example.concurrent.lock;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundQueue<T> {

    private Object[] items;
    private int addIndex, removeIndex, count;

    private Lock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();

    public BoundQueue(int size) {
        items = new Object[size];
    }

    public void add(T t) throws InterruptedException {
        lock.lock();
        try{
            // 容器满了则写线程等待
            while (count == items.length){
                notFull.await();
            }
            items[addIndex]  = t;
            if (++addIndex == items.length){
                addIndex = 0;
            }
            ++count;
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
    }

    public T remove() throws InterruptedException{
        lock.lock();
        try{
            // 容器为空则读线程等待
            while(count == 0){
                notEmpty.await();
            }
            Object x = items[removeIndex];
            if(++removeIndex == items.length){
                --count;
            }
            removeIndex = 0;
            notFull.signal();

            return (T)x;
        }finally{
            lock.unlock();
        }
    }
}
