# CountDownLatch

CountDownLatch 允许一个或多个线程等待其他线程完成操作。CountDownLatch 的构造函数接收一个 int 类型的参数作为计数器，表示允许等待的线程数，当调用 ```countDown()``` 方法时计数器的值就会减 1，CountDownLatch 的 ```await()``` 方法会阻塞当前线程直到计数器的值为 0。**CountDownLatch 的计数器不能重新初始化或者修改**。
```java
public class CountDownLatchTest{
    static CountDownLatch c = new CountDownLatch(2);

    public static void main(String[] args){
        new Thread(() -> {
            System.out.println("1");
            c.countDown();
            System.out.println("2");
            c.countDown();
        }).start();

        c.await();
        System.ouot.println("3")
    }
}
```
CountDownLatch 底层实际是使用了共享锁来完成的，CountDownLatch 内部定义了继承自 AbstractQueuedSynchronizer 的静态内部类 Sync 并重写了 tryAcquireShared 和 tryReleaseShared 方法。CountDownLatch 的 ```countDown()``` 方法和 ```await()``` 方法实际就是释放共享锁和获取共享锁的过程：
```java
private static final class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 4982264981922014374L;

    Sync(int count) {
        setState(count);
    }

    int getCount() {
        return getState();
    }
    
    // await 方法调用，只有当 state ！= 0 则线程阻塞
    protected int tryAcquireShared(int acquires) {
        return (getState() == 0) ? 1 : -1;
    }

    // countDown 方法调用，每次将 state 减一
    protected boolean tryReleaseShared(int releases) {
        // Decrement count; signal when transition to zero
        for (;;) {
            int c = getState();
            if (c == 0)
                return false;
            int nextc = c-1;
            if (compareAndSetState(c, nextc))
                return nextc == 0;
        }
    }
}
```