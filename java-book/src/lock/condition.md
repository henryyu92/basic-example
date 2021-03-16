## Condition

Condition 接口提供了类似 Object 的监视器方法，与 Lock 配合可以实现 等待/通知 模型。Condition 接口定义了 等待 和 通知两种类型的方法，线程调用这些方法前需要获取到 Condition 对象关联的锁。


Condition 对象是由 Lock 对象调用 newCondition 方法创建出来的，也就是说 Condition 对象是依赖 Lock 对象的。一般都会将 Condition 对象作为成员变量，线程获取到锁并调用 await 方法后当前线程会释放锁并在此等待，而其他线程调用 Condition 对象的 signal 方法通知当前线程后，当前线程才从 await 方法返回并且返回前已经获取了锁。
```java
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();

public void conditionWait() throw InterruptedException{
    // 获取到锁
    lock.lock();
    try{
        // 释放锁并在此等待
        condition.await();
    }finally{
        lock.unlock();
    }
}

public void conditionSignal() throw InterruptedException{
    // 获取到锁
    lock.lock();
    try{
        // 通知等待在 condition 的线程可以竞争锁，此时并未释放锁
        condition.signal();
    }finally{
        lock.unlock();
    }
}
```
ConditionObject 是 AbstracQueuedSynchronizer 的内部类，同时也实现了 Condition 接口，Java 中通过 AQS 框架实现的锁的 newCondition 方法返回的都是 ConditionObject 对象。

### 等待队列

每个 Condition 对象都包含一个等待队列，当线程调用 Condition 对象的 await 方法，该线程就会加入等待队列并等待，直到其他线程调用 Condition 对象的 signal 方法将其移出队列。

等待队列是一个 FIFO 的队列，在队列中的每个节点都包含了一个线程引用，该线程就是在 Condition 对象上等待的线程；如果一个线程调用了 ```await``` 方法，那么该线程将会释放锁、构造成节点加入等待队列并进入等待状态。

Condition 拥有头节点 (firstWaiter) 和尾节点 (lastWaiter) 的引用，新增节点只需要将原有的尾节点的 lastWaiter 指向它并且更新尾节点即可，尾节点的更新不需要使用 CAS 保证是因为调用 ```await``` 方法的线程必定是获取了锁的线程，也就是说该过程是由锁来保证线程安全的。
```java
public class ConditionObject implements Condition{
    // Condition 等待队列的首节点
    private transient Node firstWaiter;
    // Condition 等待队列的尾节点
    private transient Node lastWaiter;
}
```

### 等待

线程调用 Condition 对象的 ```await``` 方法就会被封装成 Node 加入等待队列并且释放锁，同时线程状态变为等待状态；当从```await```方法返回时，当前线程一定获取了 Condition 相关联的锁。
```java
public final void await() throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    // 将当前线程封装成 Node 加入队列
    Node node = addConditionWaiter();
    // 释放当前线程持有的锁
    int savedState = fullyRelease(node);
    int interruptMode = 0;
    // 如果当前线程不在 AQS 同步队列中(成功释放锁)，当前线程等待
    while (!isOnSyncQueue(node)) {
        LockSupport.park(this);
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    // 如果当前线程在 AQS 同步队列中(signal 将其移到同步队列)，尝试获取锁，获取成功返回 false
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    if (node.nextWaiter != null) // clean up if cancelled
        // 清除等待队列中 waitStatus 不是 CONDITION 的 Node
        unlinkCancelledWaiters();
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
}
```
调用 ``` await``` 方法的线程已经成功获取了锁，```await```方法中首先调用 ```addConditionWaiter``` 方法将当前线程构造成节点并加入等待队列中，之后调用 ```fullyRelease``` 方法释放同步状态并唤醒 AQS 同步队列中的后继节点。
```java
private Node addConditionWaiter() {
    Node t = lastWaiter;
    // If lastWaiter is cancelled, clean out.
    if (t != null && t.waitStatus != Node.CONDITION) {
        unlinkCancelledWaiters();
        t = lastWaiter;
    }
    // 将当先线程封装成 Node
    Node node = new Node(Thread.currentThread(), Node.CONDITION);
    // 将封装的 Node 加入等待队列
    if (t == null)
        firstWaiter = node;
    else
        t.nextWaiter = node;
    lastWaiter = node;
    return node;
}

final long fullyRelease(Node node) {
    boolean failed = true;
    try {
        long savedState = getState();
        // 释放同步状态
        if (release(savedState)) {
            failed = false;
            return savedState;
        } else {
            throw new IllegalMonitorStateException();
        }
    } finally {
        // 如果释放锁失败，则说明在等待队列中的 Node 有问题，后续需要清除
        if (failed)
            node.waitStatus = Node.CANCELLED;
    }
}
```

由于当前线程已经从 AQS 同步队列中移出，所以 while 循环中 ```isOnSyncQueue``` 方法返回 false，此时线程进入等待状态直到其他线程调用 ```signal``` 方法将该线程节点重新加入到 AQS 同步队列中。
```java
final boolean isOnSyncQueue(Node node) {
    // waitStatus 为 CONDITION 的 Node 在等待队列中
    // 前驱节点为 null 的 Node 是获取到锁的节点
    if (node.waitStatus == Node.CONDITION || node.prev == null)
        return false;
    if (node.next != null) // If has successor, it must be on queue
        return true;
    // 从队尾开始反向遍历 AQS 同步队列，查找 Node
    return findNodeFromTail(node);
}
```

当 while 循环条件不满足时，即当前线程节点被加入到了 AQS 同步队列中，此时当前线程通过 ```acquireQueued``` 方法从 AQS 同步队列中获取锁，如果获取锁成功则返回 false 否则继续等待或者中断。当线程获取到 Condition 对应的锁之后，会将当前线程 Node 从等待队列中移出。
```java
final boolean acquireQueued(final Node node, long arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            // 当前节点获取到锁之后返回 false
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
成功获取同步状态之后，被唤醒的线程将从 Condition 对象的 ```await``` 方法返回，此时该线程已经成功获取了锁。

### 通知

线程调用 Condition 对象的 ```signal``` 方法将会唤醒在等待队列中等待时间最长的节点(firstWaiter 节点)，在唤醒节点之前，会将节点移到同步队列中。

线程调用 Condition 对象的 signal 方法之前必须是获取到了锁(使用 ```isHeldExclusively()```检查)，然后将等待队列的头节点 firstWaiter 移到同步队列并使用 LockSupport 唤醒节点中的线程。
```java
public final void signal() {
    // 校验锁的排他性
    if (!isHeldExclusively())
        throw new IllegalMonitorStateException();
    Node first = firstWaiter;
    if (first != null)
        doSignal(first);
}
```

移动节点到同步队列并唤醒节点中线程的操作在 ```doSignal``` 方法中完成，首先将 firstWaiter 节点的后继设置为 null，然后将 firstWaiter 节点使用 ```transgerForSignal``` 方法移动到同步队列中，最后 firstWaiter 指针指向下一个节点，完成首节点的移动：
```java
private void doSignal(Node first) {
    do {
        if ( (firstWaiter = first.nextWaiter) == null)
            lastWaiter = null;
        // firstWaiter 节点的后继设置为 null
        first.nextWaiter = null;
    } while (!transferForSignal(first) &&
        (first = firstWaiter) != null);
}
```

节点在移动到同步队列之前需要先将 waitStatus 置为 0，当节点移动到同步队列中后，当前线程再使用 ```LockSupport.unpakr()``` 唤醒该节点的线程。
```java
final boolean transferForSignal(Node node) {

    if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
        return false;

    Node p = enq(node);
    int ws = p.waitStatus;
    if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
        LockSupport.unpark(node.example.thread);
    return true;
}
```

Condition 对象的 ```signalAll``` 方法相当于对等待队列中的每个节点都执行了一个 ```signal``` 方法，效果就是将等待队列中的所有节点全部移动到同步队列中，并唤醒每个节点的线程。
