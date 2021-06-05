## 线程池

Java 线程和操作系统线程是一对一的，线程的创建、调度以及销毁会消耗 CPU 资源，因此如果创建大量短生命周期的线程会降低系统的性能。

Java 提供了线程池用于异步的执行任务，使用线程池技术有多个好处：

- 通过重复利用已经创建的线程降低创建和销毁线程造成的资源消耗
- 利用阻塞队列以及拒绝策略避免任务积累导致内存溢出
- 监控和管理线程

`ThreadPoolExecutor` 和 `ScheduledThreadPoolExecutor` 是线程池体系中核心的组件，其中 `ThreadPoolExecutor` 用于执行异步的任务，而 `ScheduledThreadPoolExecutor` 能够周期性的执行异步任务。

### 创建线程池

`ScheduledThreadPoolExecutor` 继承自 `ThreadPoolExecutor`，它们在创建时需要指定几个核心参数，根据不同的参数可以创建不同策略的线程池。
```java
public ThreadPoolExecutor(int corePoolSize,
                            int maximumPoolSize,
                            long keepAliveTime,
                            TimeUnit unit,
                            BlockingQueue<Runnable> workQueue,
                            ThreadFactory threadFactory,
                            RejectedExecutionHandler handler) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.acc = System.getSecurityManager() == null ?
            null :
            AccessController.getContext();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```
- `corePoolSize` 表示线程池中保持的线程的数量，当任务提交到线程池时如果当前工作线程数小于 `corePoolSize` 那么即使其他线程空闲也会创建新线程执行任务。
- `maximumPoolSize` 表示线程池允许创建的最大线程数，如果队列满了并且已创建的线程数小于最大线程数就会创建新线程执行任务
- `keepAliveTime` 表示超过 `corePoolSize` 的空闲线程保持存活的最长时间，当任务较多可以适当调大空闲线程的存活时间
- `unit` 表示线程存活时间的单位
- `workQueue` 是一个阻塞队列， 当工作线程数超过 corePoolSize，新提交的任务会加入此阻塞队列，常用的阻塞队列有：
  - `ArrayBlockingQueue` 是一个基于数组结构的有界阻塞队列，按照 FIFO 原则对元素进行排序
  - `LinkedBlockingQueue` 是一个基于链表的阻塞队列，按照 FIFO 排序元素，吞吐量通常高于 ArrayBlockingQueue(内部采用读写锁，而 ArrayBlockingQueue 内部采用的是一个排他锁)。静态工厂方法 ```Executors.newFixedThreadPool``` 使用这个队列作为阻塞队列
  - `SynchronousQueue` 是一个不存储元素的阻塞队列，每个插入操作必须等到另一个线程调用移除操作，否则一直阻塞，内部采用的是无锁实现，因此吞吐量较高。静态工厂方法 ```Executors.newCachedThreadPool``` 使用这个队列作为阻塞队列
  - `PriorityBlockingQueue` 是一个具有优先级的无限阻塞队列
- `threadFactory` 是线程池创建线程的工厂，可以通过这个工厂给创建的线程设置有意义的名字
- `handler` 是线程池的拒绝策略，当阻塞队列和线程池都满了，对新提交的任务采用拒绝策略。Java 提供了 4 种拒绝策略，默认采用 AbortPolicy，也可以实现 ```RejectedExecutionHandler``` 自定义拒绝策略：
  - `AbortPolicy` 表示直接抛出异常
  - `CallerRunsPolicy` 表示执行 execute 方法所在线程来运行任务
  - ```DiscardOldestPolicy``` 表示丢弃阻塞队列头的任务，并执行当前任务
  - `DiscardPolicy` 表示直接丢弃当前任务

默认情况下小于 `corePoolSize` 的线程不会销毁，如果需要使线程能够被销毁则需要将 `allowCoreThreadTimeOut` 设置为 true：

```java
pool.allowCoreThreadTimeOut(true);
```



### 任务提交

线程池创建完成之后，只需要向线程池提交任务即可，线程池会自动分配线程处理提交的任务。线程池支持两种任务提交方式：

- ```execute``` 方法用于提交不需要返回值的任务，无法判断任务是否执行成功
- ```submit``` 方法用于提交需要返回值的任务，返回 Future 作为任务的结果，使用 get 方法可以阻塞的等待任务执行完返回结果

### 任务处理

任务通过 ```submit``` 或者 ```execute``` 提交到线程池之后，线程池根据创建时的参数进行调度任务的执行.

```
                +-----------+
                |  提交任务  |
                +-----------+
                    |
        +---------------------------+     否    +--------------------+
        |  线程数 >= corePoolSize ?  |----------|  创建新线程执行任务  |
        +---------------------------+           +--------------------+
                    | 是
        +---------------------------+     否    +--------------------+
        |    workQueue 是否已满 ?    |----------|  添加任务到阻塞队列  |
        +---------------------------+           +--------------------+
                    | 是
        +----------------------------+    否    +--------------------+
        | 线程数 >= maximumPoolSize ? |---------|  创建新线程执行任务  |
        +----------------------------+          +--------------------+
                    | 是
        +----------------------------+
        |       拒绝策略              |
        +----------------------------+
```
- 如果线程池中的工作线程少于 corePoolSize 则创建新的线程处理任务，否则进行下一步处理
- 如果阻塞队列 workQueue 还没有满，则将任务添加到阻塞队列，否则进行下一步处理
- 如果线程池中工作线程少于 maximumPoolSize 则创建新的线程用于处理任务，否则进行下一步
- 线程池调用 ```RejectedExecutionHandler.rejectedExecution``` 方法采用设定的拒绝策略对任务进行拒绝处理

```java
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();
    // 获取工作线程
    int c = ctl.get();
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(command, true))
            return;
        c = ctl.get();
    }
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        if (! isRunning(recheck) && remove(command))
            reject(command);
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    else if (!addWorker(command, false))
        reject(command);
}
```

### 关闭线程池

调用 ```shutdown``` 或者 ```shutdownNow``` 方法可以关闭线程池，其内部通过遍历线程池中的工作线程然后调用线程的 ```interrupt``` 方法来中断线程。

- ```shutdownNow``` 方法首先将线程池的状态设置为 STOP 然后尝试停止所有的正在执行或暂停任务的线程并返回等待执行任务的列表

- ```shutdown``` 方法将线程池的状态设置为 SHUTDOWN 然后中断所有没有执行任务的线程。

线程池关闭后，调用 ```isShutdown``` 方法就会返回 true，但是只有所有的任务都已经关闭后才表示线程池成功关闭，此时调用 ```isTerminate``` 返回 true。

```java
// 调用 shutdown 之后线程池并未立即完全关闭
threadPool.shutdown();
// 等待线程池完全关闭
threadPool.awaitTermination(long timeout, TimeUnit unit)
```



### 线程池监控

线程池提供了可以监控线程池状态的方法：
- ```getTaskCount```：线程池需要执行的任务数量
- ```getCompletedTaskCount```：线程池运行过程中已完成的任务数量
- ```getLargestPoolSize```：线程池曾经创建过的最大线程数量
- ```getPoolSize```：线程池的线程数
- ```getActiveCount```：获取活动线程数

通过继承线程池来自定义线程池，重写线程池的 ```beforeExecute```、```afterExecute``` 和 ```terminated``` 方法可以在任务执行前、执行后和线程池关闭前执行一些代码进行监控。
```java

```

### 线程池调优
创建线程池时根据不同的应用场景选择合适的参数可以达到更佳的性能：

- 对于不同性质的任务，使用不同的线程池处理。CPU 密集型任务应配置尽可能少的线程，如配置 CPU 核数 + 1 个线程的线程池；IO 密集型任务需要配置尽可能多的线程，如 2*CPU 核数 个线程的线程池；混合型任务尽可能拆分成 CPU 密集型任务和 IO 密集型任务后再处理

- 任务具有优先级时可以优先级队列 ```PriorityBlockingQueue```，这样高优先级的任务会优先执行。此时需要注意低优先级任务可能会很长时间都得不到执行的机会，可以将优先级划分为不同的级别，不同级别的任务使用不同的线程池

- 对于执行时间不同的任务也可以划分为多个时间等级，不同等级的任务使用不同的线程池执行

- 对于有外部依赖的任务，若外部依赖导致任务出现较长时间的等待则可以将线程池中的线程数调大，这样 CPU 的利用率会得到更好的使用

- 线程池的阻塞队列用于存储等待执行的任务，如果使用无界队列的话可能会导致在有大量任务而线程池负载较重的情况下线程池会积压大量任务，从而导致内存溢出；而使用有界队列时当队列满了就会触发拒绝策略而不会导致大量积压。