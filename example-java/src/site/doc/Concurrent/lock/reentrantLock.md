
### 重入锁
重入锁 ReentrantLock 是支持重进入的锁，也就是说该锁能够支持一个线程对资源的重复加锁。ReentrantLock 不能像 synchronized 关键字一样支持隐式的重进入，但是对于已经获取到锁的线程可以再次调用 ```lock()``` 方法获取锁而不会被阻塞。实现可重入锁需要解决两个问题：
- **线程再次获取到锁**，锁需要识别获取锁的线程是否为当前占据锁的线程，如果是则再次成功获取
- **锁的最终释放**，线程重复 N 次获取了锁，随后第 N 次释放该锁后其他线程能够获取到该锁

ReentrantLock 通过组合自定义同步器来实现锁的获取与释放：
```java
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        // 获取锁的线程再次请求，所以不需要 CAS 保证
        setState(nextc);
        return true;
    }
    return false;
}
```
该方法增加了再次获取同步状态的处理逻辑：判断当前线程是否是已经获取锁的线程，如果是获取锁的线程再次请求则将同步状态值进行增加并返回 true 表示再次获取同步状态成功。

成功获取锁的线程再次获取锁，只是增加了同步状态值，因此释放锁的时候需要减少同步状态值，因为线程已经获取到锁所以此处对同步状态值的修改不需要 CAS 保证：
```java
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    boolean free = false;
    if (c == 0) {
        free = true;
        setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
}
```
如果该锁被释放了 N 次，那么前 N-1 次 ```tryRelease(int)``` 必须返回 false，而只有同步状态完全释放了才能返回 true。该方法将同步状态是否为 0 作为最终释放的条件，当同步状态为 0 时，将占有线程设置为 null 并返回 true 表示释放成功。
### 公平锁与非公平锁
公平性与否是针对获取锁的线程而言的，如果一个锁是公平的那么获取锁的顺序就应该符合请求的绝对时间顺序，也就是 FIFO；以 ReentrantLock 中的公平锁为例，tryAcquire 中增加对是有前驱节点的判断：
```java
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (!hasQueuedPredecessors() &&
            compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}



public final boolean hasQueuedPredecessors() {
    // The correctness of this depends on head being initialized
    // before tail and on head.next being accurate if the current
    // thread is first in queue.
    Node t = tail; // Read fields in reverse initialization order
    Node h = head;
    Node s;
    return h != t &&
        ((s = h.next) == null || s.thread != Thread.currentThread());
}
```
公平锁的 ```tryAcquire``` 与非公平锁的 ```nofairTryAcquire``` 方法唯一不同的是判断条件多了 ```hasQueuedPredecessors()``` 方法，即判断同步队列当中当前节点是否有前驱结点，如果返回 true 表示当前线程不是队列首节点则需要等待前驱结点获取锁并释放锁之后才能获取锁

**公平性锁保证了锁的获取顺序按照 FIFO 原则，代价是进行大量的线程切换；而非公平性锁虽然可能造成线程“饥饿”，但是极少的线程切换保证了更大的吞吐量。**

**[Back](../../)**