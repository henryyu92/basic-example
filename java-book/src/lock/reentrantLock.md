## 重入锁

重入锁指的是支持重进入的锁，也就是说重入锁能够支持一个线程对资源的重复加锁。实现锁的可重入需要保证：

- 获取到锁的线程可以再次获取到锁
- 线程对获取了 N 次锁需要释放 N 次才能完全释放获取的锁

Java 关键字 synchronized 隐式支持重入，因此线程在获取到了锁之后再次调用加锁的方法时不会阻塞。除了 synchronized 关键字，Java 还提供了基于 `AQS` 框架实现的重入锁 ```ReentrantLock```，线程使用 lock 方法获取到锁之后可以再次调用 lock 方法获取锁而不会被阻塞。

`ReentrantLock` 内部类 `Sync` 继承自 `AbstractQueuedSynchronizer`，在 `tryAcquire` 方法中如果不是首次获取锁则会判断当前线程是否已经获取到锁，如果是则会将同步状态值增加并返回 true 表示再次获取锁成功。
```java
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    // 未被锁定则获取锁
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    // 如果当前线程已经获取到锁
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
线程多次获取锁会使同步状态增加，因此释放时需要多次释放使得同步状态为 0 才算是真正释放了锁。释放锁的过程不需要通过 CAS 保证，因为线程已经获取到了锁，不会出现竞争。
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
    // 减小同步状态
    setState(c);
    return free;
}
```
如果该锁被释放了 N 次，那么前 N-1 次 ```tryRelease``` 必须返回 false，而只有同步状态完全释放了才能返回 true。该方法将同步状态是否为 0 作为最终释放的条件，当同步状态为 0 时，将占有线程设置为 null 并返回 true 表示释放成功。

### 公平与非公平

锁的公平性与否是针对获取锁的线程而言的，如果一个锁是公平的那么获取锁的顺序就应该是线程请求锁的顺序；以 `ReentrantLock` 中的公平锁为例，`tryAcquire` 中增加对是有前驱节点的判断：
```java
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        // 队列中没有前驱才会尝试获取锁
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
