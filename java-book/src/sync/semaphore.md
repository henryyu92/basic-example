# Semaphore

Semaphore(信号量) 用来控制同时访问特定资源的线程数量，它通过协调各个线程保证合理的使用公共资源。

Semaphore 的构造函数接收一个整型参数表示可用的许可证数量，也就是最大并发数量。使用时通过 `acquire()` 获取许可并且在完成逻辑之后通过 `release()` 方法归还许可，如果没有获取到许可则当前线程阻塞。
```java
public class SemaphoreTest{

    private static final int THREAD_COUNT = 30;
    private static Semaphore s = new Semaphore(10);

    public static void main(String[] args){
        for(int i = 0; i < THREAD_COUNT; i++){
            new Thread(new Runnable(){
                public void run(){
                    try{
                        s.acquire();
                        System.out.println("save data");
                        s.release();
                    }catch(InterruptedException e){

                    }
                }
            }).start();
        }
    }
}
```
Semaphore 还提供了其他获取当前信号量状态的方法：
- `availablePermits()`：返回此信号量中当前可用的许可证
- `getQueueLength()`：返回正在等待获取许可证的线程数
- `hasQueuedThreads()`：是否有线程正在等待获取许可证

Semaphore 是通过 AQS 实现的，许可证数量作为 AQS 的 state 变量。每次获取许可时对 state 作 CAS 减操作，当 state 变为 0 时则表明许可耗尽，再次获取许可时需要阻塞线程；当获取到许可的线程执行完逻辑调用 `release()` 方法归还许可时则对 state 作 CAS 加操作。
```java
// 获取许可
public void acquire(int permits) throws InterruptedException {
    if (permits < 0) throw new IllegalArgumentException();
    sync.acquireSharedInterruptibly(permits);
}

// 归还许可
public void release(int permits) {
    if (permits < 0) throw new IllegalArgumentException();
    sync.releaseShared(permits);
}
```