# BlockingQueue

阻塞队列(BlockingQueue)是作支持阻塞的插入和移除的队列。阻塞的插入是当队列满时，队列会阻塞插入元素的线程直到线程 NotFull；阻塞的移除是当队列为空时，获取元素的线程会等待直到队列变为 NotEmpty。

阻塞队列常用于生产者和消费者的场景，生产者是向队列里添加元素的线程，消费者是从队列里获取元素的线程，阻塞队列用于生产者存放元素、消费者获取元素的容器。**生产者和消费者在生产和消费的过程中能够高效的通信是因为阻塞队列中大量使用了等待通知模型**，当生产者往满队列里添加元素时会阻塞线程，当消费者消费了队列元素时会通知生产者当前队列可用，等待通知使用了 ReentrantLock 和 Condition 来实现。

阻塞队列提供了多种添加和移除的操作：
- `add`：向队列尾添加元素，如果队列已满则抛出 ```IllegalStateException```
- `offer`：向队列尾添加元素，如果成功则返回 true，如果失败返回 false
- `put`：向队列尾添加元素，如果队列已满则阻塞
- `remove`：移除队列的头的元素，如果队列为空则抛出 ```NoSuchElementException```
- `poll`：移除队列头的元素，如果成功返回这个元素，如果失败放回 null
- `peek`：获取但不删除队列头的元素，如果成功则返回这个元素，如果失败则返回 null
- `take`：移除队列头元素，如果队列为空则一直阻塞直到队列不为空
### ArrayBlockingQueue
ArrayBlockingQueue 是一个由数组结构组成的有界阻塞队列，此队列按照先进先出(FIFO)的原则对元素进行排序。默认情况下不保证线程公平的访问队列，也可以创建公平的阻塞队列：
```java
public ArrayBlockingQueue(int capacity, boolean fair) {
    if (capacity <= 0)
        throw new IllegalArgumentException();
    this.items = new Object[capacity];
    lock = new ReentrantLock(fair);
    notEmpty = lock.newCondition();
    notFull =  lock.newCondition();
}
```
ArrayBlockingQueue 向队列中添加元素时先要获取到锁，然后判断队列是否满，如果队列为满则一直等待直到其他线程调用 ```notFull.signal``` 告知队列不满，否则进行入队操作，入队操作是会调用 ```notEmpty.signal()``` 方法告知其他出队线程队列非空：
```java
public void put(E e) throws InterruptedException {
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
	// 获取锁除非当前线程中断
    lock.lockInterruptibly();
    try {
        while (count == items.length)
            notFull.await();
        enqueue(e);
    } finally {
        lock.unlock();
    }
}
```
ArrayBlockingQueue 的出队操作也需要先获取到锁，然后判断队列是否为空，如果为空则一直等待直到其他线程调用 ```notEmpty.signal()``` 告知队列非空，否则进行出队操作，出队操作会调用 ```notFull.signal()``` 方法告知其他入队线程队列非满：
```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (count == 0)
            notEmpty.await();
        return dequeue();
    } finally {
        lock.unlock();
    }
}
```
### LinkedBlockingQueue

LinkedBlockingQueue 是一个用链表实现的有界阻塞队列，此队列的默认长度和最大长度为 `Integer.MAX_VALUE`，此队列按照先进先出的原则对元素进行排序。LinkedBlockingQueue 持有两把锁 putLock 和 takeLock 分别作用于向队列添加元素和移除元素，两把锁的方式可以减少所竞争。

LinkedBlockingQueue 的入队操作先要获取 putLock，然后判断队列是否满，如果是则等待否则入队并且判断入队之后队列是否满，此处判断是因为读线程只会在队列满了并移除了元素才会通知写线程，而队列从满到不满之间可能会有多个读线程移除元素，所以写线程在发现队列不满时需要通知其他写线程队列可写：
```java
public void put(E e) throws InterruptedException {
    if (e == null) throw new NullPointerException();
    // Note: convention in all put/take/etc is to preset local var
    // holding count negative to indicate failure unless set.
    int c = -1;
    Node<E> node = new Node<E>(e);
    final ReentrantLock putLock = this.putLock;
    final AtomicInteger count = this.count;
    putLock.lockInterruptibly();
    try {
        while (count.get() == capacity) {
            notFull.await();
        }
        enqueue(node);
        c = count.getAndIncrement();
        if (c + 1 < capacity)
            notFull.signal();
    } finally {
        putLock.unlock();
    }
    if (c == 0)
        signalNotEmpty();
}
```
LinkedBlockingQueue 的出队操作先要获取 takeLock，然后判断队列是否为空，空则等待否则入队并再度检查队列是否为空，此处是因为写线程在队列为空并写入一个元素之后才会通知读线程，此时队列可能不为空但是有多个读线程仍然在 wait，因此当判断队列不为空时需要通知唤醒这些等待的读线程：
```java
public E take() throws InterruptedException {
    E x;
    int c = -1;
    final AtomicInteger count = this.count;
    final ReentrantLock takeLock = this.takeLock;
    takeLock.lockInterruptibly();
    try {
        while (count.get() == 0) {
            notEmpty.await();
        }
        x = dequeue();
        c = count.getAndDecrement();
        if (c > 1)
            notEmpty.signal();
    } finally {
        takeLock.unlock();
    }
    if (c == capacity)
        signalNotFull();
    return x;
}
```
### PriorityBlockingQueue

PriorityBlockingQueue 是一个支持优先级的无界阻塞队列，默认情况下元素采取自然顺序升序排列，也可以自定义类实现 `compareTo()` 方法来指定元素排序规则，或者初始化 PriorityBlockingQueue 时指定构造参数 Comparator 来对元素进行排序。

入队
```java
public boolean offer(E e) {
    if (e == null)
        throw new NullPointerException();
    final ReentrantLock lock = this.lock;
    lock.lock();
    int n, cap;
    Object[] array;
    while ((n = size) >= (cap = (array = queue).length))
        // 容量超出时需要扩容
        tryGrow(array, cap);
    try {
        Comparator<? super E> cmp = comparator;
        if (cmp == null)
            siftUpComparable(n, e, array);
        else
            siftUpUsingComparator(n, e, array, cmp);
        size = n + 1;
        notEmpty.signal();
    } finally {
        lock.unlock();
    }
    return true;
}
```
出队时需要先获取锁对象，然后将元素出队，如果元素出队失败则需要等待入队线程的通知：
```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    E result;
    try {
        while ( (result = dequeue()) == null)
            // 出队失败则表示没有元素
            notEmpty.await();
    } finally {
        lock.unlock();
    }
    return result;
}
```
### DelayQueue
DelayQueue 是一个支持延时获取元素的无界阻塞队列，队列使用 PriorityQueue 来实现，队列中的元素必须实现 Delayed 接口，在创建元素时可以指定多久才能从队列中获取当前元素，只有在延迟期满才能从队列中提取元素。
  
DelayQueue 可以应用在多个场景：
- 缓存系统的设计：使用 DelayQueue 保存缓存元素的有效期，一个线程循环查询 DelayQueue 一旦能从 DelayQueue 中获取元素表示缓存有效期到了
- 定时任务调度：使用 DelayQueue 保存当天将会执行的任务和执行时间，一旦从 DelayQueue 中获取到任务就开始执行

添加到 DelayQueue 中的元素需要实现 Delayed 接口并且在实现类中重写方法：
- `getDelay()`：返回当前元素还需要延时多少时间，单位是纳秒。如果小于 0 则表示当前延迟元素需要出队
- `compareTo()`：指定入队元素的顺序

元素出队时线程会不断从优先队列中取出元素，如果队列为空则阻塞线程等待写入线程通知。如果取出的元素延迟时间到达则出队，否则继续等待。
  ```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        for (;;) {
            // 从优先队列中取出元素
            E first = q.peek();
            // 队头没有元素线程阻塞
            if (first == null)
                available.await();
            else {
                // 如果元素延时到达则出队
                long delay = first.getDelay(NANOSECONDS);
                if (delay <= 0)
                    return q.poll();
                first = null; // don't retain ref while waiting
                // leader 是一个等待获取队列头元素的线程，如果 leader 不为 null 说明已经有线程在等待获取队列头元素则当前线程直接阻塞等待，
                // 如果 leader 为 null 说明没有线程等待则将当前线程设置为 leader，并使用 awaitNanos 方法让线程等待接收信号或等待 delay 时间
                if (leader != null)
                    available.await();
                else {
                    Thread thisThread = Thread.currentThread();
                    leader = thisThread;
                    try {
                        available.awaitNanos(delay);
                    } finally {
                        if (leader == thisThread)
                            leader = null;
                    }
                }
            }
        }
    } finally {
        if (leader == null && q.peek() != null)
            available.signal();
        lock.unlock();
    }
}
```

### SynchronousQueue
SynchronousQueue 是一个不存储元素的阻塞队列，每一个 put 操作必须等待一个 take 操作，否则不能继续添加元素。

SynchronousQueue 负责把生产者线程处理的数据直接传递给消费者，适合高吞吐量的场景。默认情况下采用非公平策略访问队列，可以在构造时设置为公平策略：
```java
public SynchronousQueue(boolean fair) {
    transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
}
```
SynchronousQueue 的入队和出队都是使用 Transfer 的 transfer 方法实现，非公平策略的阻塞队列的实现是 TransferStack，公平策略的阻塞队列的实现是 TransferQueue


```java
public void put(E e) throws InterruptedException {
    if (e == null) throw new NullPointerException();
    if (transferer.transfer(e, false, 0) == null) {
        Thread.interrupted();
        throw new InterruptedException();
    }
}
```
出队
```java
public E take() throws InterruptedException {
    E e = transferer.transfer(null, false, 0);
    if (e != null)
        return e;
    Thread.interrupted();
    throw new InterruptedException();
}
```
### LinkedTransferQueue
LinkedTransferQueue 是由一个链表结构组成的无界阻塞 TransferQueue 队列，相对于其他阻塞队列， LinkedTransferQueue 提供了 ```tryTransfer``` 和 ```transfer``` 方法
- **transfer 方法**：如果当前有消费者正在等待接收元素，transfer 方法可以把生产者传入的元素立即 transfer 给消费者；如果没有消费者在等待接收元素，transfer 方法将元素存放在队列尾直到该元素被消费者消费了才返回，核心代码如下：
```java
Node pred = tryAppend(s, haveData);

return awaitMatch(s, pred, e, (how == TIMED), nanos);
```
第一行代码试图把存放当前元素的节点 s 设置为尾节点；第二行代码是让 CPU 自旋等待消费者消费元素，因为自旋会消耗 CPU 所以自旋一定次数后使用 Thread.yield() 方法来暂停当前正在执行的线程
- **tryTransfer 方法**：tryTransfer 方法是用来试探生产者传入的元素是否能直接传给消费者，如果没有消费者等待接收元素则返回 false；和 transfer 方法不同的是该方法无论消费者是否接收都会立即返回。
### LinkedBlockingDeque
LinkedBlockingDeque 是一个由链表结构组成的双端阻塞队列，即可以从队列的两端插入和移除元素。在初始化 LinkedBlockingDequeue 时可以设置容量防止其过度膨胀。
```java
```