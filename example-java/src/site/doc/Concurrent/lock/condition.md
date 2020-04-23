### Condition
Condition 对象是由 Lock 对象调用 newCondition 方法创建出来的，也就是说 Condition 对象是依赖 Lock 对象的。一般都会将 Condition 对象作为成员变量，当 Lock 对象调用 await 方法后当前线程会释放锁并在此等待，而其他线程调用 Condition 对象的 signal 方法(signal 方法不会释放锁)通知当前线程后，当前线程才从 await 方法返回并且返回前已经获取了锁。
```java
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();

public void conditionWait() throw InterruptedException{
    lock.lock();
    try{
        condition.await();
    }finally{
        lock.unlock();
    }
}

public void conditionSignal() throw InterruptedException{
    lock.lock();
    try{
        condition.signal();
    }finally{
        lock.unlock();
    }
}
```
#### 有界队列中的 Condition 
在添加数据时，如果数组数量等于数组长度时表示数组已满则释放锁进入等待状态，如果数组不满则添加元素到数组中并通知等待在 NotEmpty 的线程数组已经有新元素可以获取；在删除元素时，如果数组为空则需要释放锁并阻塞当前线程，否则在添加完数据后需要通知阻塞的插入线程可以插入数据；在添加和删除方法中使用 while 循环而非 if 判断是防止过早或意外的通知，只有符合条件才能跳出循环：
```java
public class BoundedQueue<T>{
    private Object[] items;
    private int addIndex, removeIndex, count;
    private Lock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();

    public BoundedQueue(int size){
        items = new Object[size];
    }

    public void add(T t) throw InterruptedException {
        lock.lock();
        try{
            while(count == items.length){
                notFull.await();
            }
            items[addIndex] = t;
            if(++addIndex == items.length){
                addIndex = 0;
            }
            ++count;
            notEmpty.signal();
        }finally{
            lock.unlock();
        }
    }

    public T remove() throw InterruptedException{
        lock.lock();
        try{
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
```
ConditionObject 是 Condition 接口的实现类，是同步器 AbstracQueuedSynchronizer 的内部类，可以从 ConditionObject 分析 Condition 的等待队列、等待和通知：
#### 等待队列
等待队列是一个 FIFO 的队列，在队列中的每个节点都包含了一个线程引用，该线程就是在 Condition 对象上等待的线程；如果一个线程调用了```await()```方法，那么该线程将会释放锁、构造成节点加入等待队列并进入等待状态。

Condition 拥有首节点(firstWaiter)和尾节点(nextWaiter)的引用，新增节点只需要将原有的尾节点的 nextWaiter 指向它并且更新尾节点即可，尾节点的更新不需要使用 CAS 保证是因为调用```await()```方法的线程必定是获取了锁的线程，也就是说该过程是由锁来保证线程安全的。
#### 等待
调用```await()```方法会使当前线程进入等待队列并且释放锁，同时线程状态变为等待状态；当从```await()```方法返回时，当前线程一定获取了 Condition 相关联的锁。
```java
public final void await() throws InterruptedException {
if (Thread.interrupted())
    throw new InterruptedException();
Node node = addConditionWaiter();
int savedState = fullyRelease(node);
int interruptMode = 0;
while (!isOnSyncQueue(node)) {
    LockSupport.park(this);
    if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
        break;
}
if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
    interruptMode = REINTERRUPT;
if (node.nextWaiter != null) // clean up if cancelled
    unlinkCancelledWaiters();
if (interruptMode != 0)
    reportInterruptAfterWait(interruptMode);
}

final boolean isOnSyncQueue(Node node) {
if (node.waitStatus == Node.CONDITION || node.prev == null)
    return false;
if (node.next != null) // If has successor, it must be on queue
    return true;
/*
    * node.prev can be non-null, but not yet on queue because
    * the CAS to place it on queue can fail. So we have to
    * traverse from tail to make sure it actually made it.  It
    * will always be near the tail in calls to this method, and
    * unless the CAS failed (which is unlikely), it will be
    * there, so we hardly ever traverse much.
    */
return findNodeFromTail(node);
}
```
调用该方法的线程是成功获取了锁的线程，也就是同步队列中的首节点，该方法会将当前线程构造成节点并加入等待队列中，然后释放同步状态并唤醒同步队列中的后继节点，然后当前线程会进入等待状态。当等待队列中的节点被唤醒，则唤醒节点的线程开始尝试获取同步状态；如果不是通过其他线程调用 ```Condition.signal()``` 方法唤醒而是对等待线程进行中断则会抛出 ```InterruptedException```。
#### 通知
调用 ```Condition.signal()``` 方法将会唤醒在等待队列中等待时间最长的节点(首节点)，在唤醒节点之前，会将节点移到同步队列中。
```java
public final void signal() {
if (!isHeldExclusively())
    throw new IllegalMonitorStateException();
Node first = firstWaiter;
if (first != null)
    doSignal(first);
}

private void doSignal(Node first) {
do {
    if ( (firstWaiter = first.nextWaiter) == null)
        lastWaiter = null;
    first.nextWaiter = null;
} while (!transferForSignal(first) &&
    (first = firstWaiter) != null);
}

final boolean transferForSignal(Node node) {
/*
    * If cannot change waitStatus, the node has been cancelled.
    */
if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
    return false;

/*
    * Splice onto queue and try to set waitStatus of predecessor to
    * indicate that thread is (probably) waiting. If cancelled or
    * attempt to set waitStatus fails, wake up to resync (in which
    * case the waitStatus can be transiently and harmlessly wrong).
    */
Node p = enq(node);
int ws = p.waitStatus;
if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
    LockSupport.unpark(node.thread);
return true;
}
```
调用该方法的前置条件是当前线程获取了锁(使用 ```isHeldExclusively()```检查)，然后获取等待队列的首节点并将其移动到同步队列并使用 LockSupport 唤醒节点中的线程。

通过调用同步器的 ```enqueue()``` 方法，等待队列中的头节点线程安全地移动到同步队列；当节点移动到同步队列中后，当前线程再使用 ```LockSupport.unpakr()``` 唤醒该节点的线程；

被唤醒后的线程将从 ```await()``` 方法中的 ```while``` 循环中退出(```isOnSyncQueue()``` 方法返回 true，节点已经在同步队列中)，进而调用同步器的 ```acquireQueued()``` 方法加入到获取同步状态的竞争中；成功获取同步状态之后，被唤醒的线程将从先前调用的 ```await()``` 方法返回，此时该线程已经成功获取了锁。

Condition 的 ```signalAll()``` 方法相当于对等待队列中的每个节点都执行了一个 ```signal()``` 方法，效果就是将等待队列中的所有节点全部移动到同步队列中，并唤醒每个节点的线程。


**[Back](../../)**