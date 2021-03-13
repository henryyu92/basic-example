# 队列同步器

队列同步器 (AbstractQueuedSynchronizer) 是用来构建同步组件的基础框架，AQS 使用一个 int 成员变量 state 表示同步状态，通过内置的 FIFO 队列完成资源获取线程的排队工作。

同步器主要使用的方式是继承，同步器提供了 3 个方法来访问和修改同步状态：
- ```int getState()```：获取当前同步状态
- ```void setState(int state)```：设置当前同步状态
- ```boolean compareAndSetState(int expect, int update)```：使用 CAS 设置当前状态，保证状态设置的原子性

子类主要通过重写同步器的方法实现锁功能，主要重写的的方法：
- ```boolean tryAcquire(int)```：独占式获取同步状态，实现该方法需要查询当前状态并判断同步状态是否符合预期，然后再进行 CAS 设置同步状态
- ```boolean tryRelease(int)```：独占式释放同步状态，等待获取同步状态的线程将有机会获取同步状态
- ```int tryAcquireShared(int)```：共享式获取同步状态，返回大于 0 的值表示获取成功，反之获取失败
- ```int tryReleaseShared(int)```：共享式释放同步状态
- ```boolean isHeldExclusively()```：当前同步器是否在独占模式下被线程占用，一般表示是否被当前线程独占

同步器对外提供的方法：
- ```void acquire(int)```：获取独占锁
- ```boolean release(int)```：释放独占锁锁
- ```void acquireShared(int)```：获取共享锁
- ```boolean releaseShared(int)```：释放共享锁

同步器子类通常定义为同步组件的静态内部类而同步组件不需要实现任何同步接口，且同步器支持独占式和共享式获取同步状态，这样接可以实现不同类型的的同步组件(ReentrantLock，ReentrantReadWriteLock 等)。

利用队列同步器实现自定义锁：
```java
public class Mutext implements Lock {

    private static class Sync extends AbstractQueuedSynchronizer{
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }
        protected boolean tryAcquire(int arg) {
            // 使用 CAS 设置 state 为 1 并设置独占线程为当前线程
            if (compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
        protected boolean tryRelease(int arg) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            // 释放锁状态时由于是持有锁的，所以可以直接修改状态
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        Condition newCondition(){return new ConditionObject();}
    }

    private final Sync sync = new Sync();
	
    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
```
#### 同步队列

AbstractQueuedSynchronizer 依赖内部的同步队列（一个 FIFO 双向队列）来完成同步状态的管理。当线程获取同步状态失败时，AbstractQueuedSynchronizer 会将当前线程以及等待状态构造成一个节点并将其加入到同步队列，同时会阻塞当前线程；当同步状态释放时，会把首节点中的线程唤醒，使其再次尝试获取同步状态。

同步队列中的节点用来保存获取同步状态失败线程的引用、等待状态以及前驱和后继结点：
```java
static final class Node {
    // 当前节点等待状态
    volatile int waitStatus;
    // 前驱节点
    volatile Node prev;
    // 后继节点
    volatile Node next;
    // 包装的线程
    volatile Thread thread;
    // 下一个等待 condition 的节点
    Node nextWaiter;

    //...
}
```
AQS 包含两个节点类型的引用：指向队列的头节点 head 和  指向队列的尾节点 tail
```java
public abstract class AbstractQueuedSynchronizer{
    // 队列头节点
    private transient volatile Node head;
    // 队列尾节点
    private transient volatile Node tail;
    // 同步状态
    private volatile int state;
}
```
当一个线程获取同步状态失败而加入同步队列时，AQS 使用 CAS 设置 tail 节点引用指向该节点；队列首节点是获取同步状态成功的节点，当首节点释放同步状态时，将 head 节点引用指向下一个节点并断开与下一个节点的关联。

#### 独占式同步状态获取

AQS 使用 acquire 方法获取同步状态，该方法对中断不敏感，也就是说如果线程获取同步状态失败进入同步队列之后对其进行中断操作不会导致节点从队列移除。
```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```
acquire 方法中首先调用 AQS 子类重写的 tryAcquire 方法保证线程安全的获取同步状态，如果获取同步状态失败则通过 addWaiter 方法构造独占式节点并加入到同步队列中，之后调用 acquireQueued 方法以死循环的方式获取同步状态，如果获取不到则阻塞节点中的线程，而阻塞线程的唤醒主要依靠前驱结点的出队或者阻塞线程被中断来实现。

addWaiter 方法将当前线程包装成一个 Node 节点加入到同步队列中，加入同步队列使用的是 CAS 方式：
```java
private Node addWaiter(Node mode) {
    // 构造队列节点
    Node node = new Node(Thread.currentThread(), mode);
    Node pred = tail;
    if (pred != null) {
        // 当前节点的前驱设置为队尾，多线程下可能会有多个节点设置
        node.prev = pred;
        // 使用 CAS 将当前节点设置为队尾
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    // 队列为空或者节点设置队尾不成功则入队
    enq(node);
    return node;
}
```
addWaiter 方法构造节点之后判断 tail 结点是否是 null，如果是则直接调用 enq 方法入队，否则先将节点的前驱设置为当前尾结点，然后使用 CAS 将 tail 结点设置为当前节点，如果 CAS 操作成功则将当前节点加入队尾，否则说明已经有节点被加入到队列尾部，调用 enq 方法将当前节点入队。
```java
private Node enq(final Node node) {
    // 无限循环直到入队成功
    for (;;) {
        Node t = tail;
        // 尾结点为 null 说明队列为空，则先创建队列，多线程下需要保证队列只有一个头结点和尾结点
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            // 当前节点的前驱设置为尾节点，即入队操作
            node.prev = t;
            // CAS 设置 node 为尾节点直到设置成功为止
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}
```
enq 方法是通过死循环的方式将线程节点加入等待队列，如果队列不存在则初始化队列，否则进行入队操作直到当前线程被设置为 tail 节点才返回；使用 compareAndSetTail 方法保证只有一个节点被设置为 tail 结点，通过死循环和 CAS 使得并发入队变得串行化了。

阻塞线程进入同步队列后就调用 acquireQueued 方法使每个节点自旋（死循环）判断前驱节点是否是 head 节点（已经获取到同步状态）并且尝试获取同步状态，如果成功则将自己设置为 head 节点返回，否则就进入等待状态直到被 pre 节点中断唤醒；
```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            // 获取当前节点的前驱节点
            final Node p = node.predecessor();
            // 如果前驱节点是 head 并且获取到同步状态则将当前节点设置为 head
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            // 进入 Waiting 状态直到被中断唤醒
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}

private final boolean parkAndCheckInterrupt() {
    LockSupport.park(this);
    return Thread.interrupted();
}
```

#### 独占式同步状态释放

通过调用 AQS 的 release 方法释放同步状态，使得其他线程能够获取同步状态；该方法在释放了同步状态之后会唤醒其后继节点。release 方法释放同步状态后，如果 head 节点不为空且等待状态不为 0 则唤醒 head 节点的后继节点：
```java
public final boolean release(int arg) {
    // 释放同步状态
    if (tryRelease(arg)) {
        Node h = head;
        // 唤醒 head 的后继节点
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

#### 共享式同步状态获取

AQS 作为 Java 中锁的基础组件，不仅提供了独占式的获取同步状态，也提供了共享式获取同步状态。共享式获取同步状态由 AQS 的 acquireShared 方法提供，该方法与独占式获取同步状态的区别在于获取同步状态时会判断同步状态的值：
```java
public final void acquireShared(int arg) {
    if (tryAcquireShared(arg) < 0)
        doAcquireShared(arg);
}

private void doAcquireShared(int arg) {
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                int r = tryAcquireShared(arg);
                if (r >= 0) {
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    if (interrupted)
                        selfInterrupt();
                    failed = false;
                    return;
                }
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
#### 共享式同步状态释放
```java
public final boolean releaseShared(int arg) {
	if (tryReleaseShared(arg)) {
		doReleaseShared();
		return true;
	}
	return false;
}

private void doReleaseShared() {
	for (;;) {
		Node h = head;
		if (h != null && h != tail) {
			int ws = h.waitStatus;
			if (ws == Node.SIGNAL) {
				if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
					continue;            // loop to recheck cases
				unparkSuccessor(h);
			}
			else if (ws == 0 &&
					 !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
				continue;                // loop on failed CAS
		}
		if (h == head)                   // loop if head changed
			break;
	}
}
```
#### 超时获取同步状态

**[Back](../../)**