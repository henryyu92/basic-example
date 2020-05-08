## 线程池

Java 线程和操作系统线程是一对一的关系，创建一个 Java 线程时会创建一个本地操作系统线程，当 Java 线程终止时本地线程也会被回收。

在大型系统中会有大量的线程存在，如果使用直接创建线程和销毁线程的方式，则会带来大量的系统开销。Java 提供了线程池使得线程可以被复用，而不是在需要的时候创建新线程，线程处理完任务后就直接销毁，从而减少了系统频繁创建线程和销毁线程带来的巨大开销。

使用线程池至少可以有 3 个方面的好处：
- 降低资源消耗：通过重复利用已创建的线程降低线程创建和销毁造成的消耗
- 提高响应效率：当任务队列到达时，任务可以不需要等到线程创建就能立即执行
- 提高线程的可管理性：使用线程池可以进一步分配、调优和监控线程

### 创建线程池

Java 中 ```ThreadPoolExecutor``` 表示线程池，通过 ```ThreadPoolExecutor``` 的构造器可以直接创建一个线程池。 
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
```ThreadPoolExecutor``` 构造器创建线程池需要传入几个核心参数：
- ```corePoolSize``` 表示线程池中保持的线程的数量，当任务提交到线程池时如果当前工作线程数小于 corePoolSize 那么即使其他线程空闲也会创建新线程执行任务。
- ```maximumPoolSize``` 表示线程池允许创建的最大线程数，如果队列满了并且已创建的线程数小于最大线程数就会创建新线程执行任务
- ```keepAliveTime``` 表示超过 corePoolSize 的空闲线程保持存活的最长时间，当任务较多可以适当调大空闲线程的存活时间
- ```unit``` 表示线程存活时间的单位
- ```workQueue``` 是一个阻塞队列， 当工作线程数超过 corePoolSize，新提交的任务会加入此阻塞队列，常用的阻塞队列有：
  - ```ArrayBlockingQueue``` 是一个基于数组结构的有界阻塞队列，按照 FIFO 原则对元素进行排序
  - ```LinkedBlockingQueue``` 是一个基于链表的阻塞队列，按照 FIFO 排序元素，吞吐量通常高于 ArrayBlockingQueue(内部采用读写锁，而 ArrayBlockingQueue 内部采用的是一个排他锁)。静态工厂方法 ```Executors.newFixedThreadPool``` 使用这个队列作为阻塞队列
  - ```SynchronousQueue``` 是一个不存储元素的阻塞队列，每个插入操作必须等到另一个线程调用移除操作，否则一直阻塞，内部采用的是无锁实现，因此吞吐量较高。静态工厂方法 ```Executors.newCachedThreadPool``` 使用这个队列作为阻塞队列
  - ```PriorityBlockingQueue``` 是一个具有优先级的无限阻塞队列
- ```threadFactory``` 是线程池创建线程的工厂，可以通过这个工厂给创建的线程设置有意义的名字
- ```handler``` 是线程池的拒绝策略，当阻塞队列和线程池都满了，对新提交的任务采用拒绝策略。Java 提供了 4 种拒绝策略，默认采用 AbortPolicy，也可以实现 ```RejectedExecutionHandler``` 自定义拒绝策略：
  - ```AbortPolicy``` 表示直接抛出异常
  - ```CallerRunsPolicy``` 表示执行 execute 方法所在线程来运行任务
  - ```DiscardOldestPolicy``` 表示丢弃阻塞队列头的任务，并执行当前任务
  - ```DiscardPolicy``` 表示直接丢弃当前任务


创建线程池时根据不同的应用场景选择合适的参数可以达到更佳的性能，线程池中任务可以从几个方面分析：
- 任务的性质：CPU 密集型任务、IO 密集型任务 和 混合型任务
- 任务的优先级
- 任务的执行时间
- 任务的依赖性：任务是否依赖其他系统资源，如数据库连接

对于不同性质的任务，使用不同的线程池处理。CPU 密集型任务应配置尽可能少的线程，如配置 CPU 核数 + 1 个线程的线程池；IO 密集型任务需要配置尽可能多的线程，如 2*CPU 核数 个线程的线程池；混合型任务尽可能拆分成 CPU 密集型任务和 IO 密集型任务后再处理

任务具有优先级时可以优先级队列 ```PriorityBlockingQueue```，这样高优先级的任务会优先执行。此时需要注意低优先级任务可能会很长时间都得不到执行的机会，可以将优先级划分为不同的级别，不同级别的任务使用不同的线程池

对于执行时间不同的任务也可以划分为多个时间等级，不同等级的任务使用不同的线程池执行

对于有外部依赖的任务，若外部依赖导致任务出现较长时间的等待则可以将线程池中的线程数调大，这样 CPU 的利用率会得到更好的使用

线程池的阻塞队列用于存储等待执行的任务，如果使用无界队列的话可能会导致在有大量任务而线程池负载较重的情况下线程池会积压大量任务，从而导致内存溢出；而使用有界队列时当队列满了就会触发拒绝策略而不会导致大量积压。

### 任务提交

线程池创建完成之后，只需要向线程池提交任务即可，线程池会自动分配线程处理提交的任务。Java 提供了两种向线程池提交任务的方法：

- ```execute``` 方法用于提交不需要返回值的任务，无法判断任务是否执行成功
- ```submit``` 方法用于提交需要返回值的任务，返回 FutureTask 的异步结果，可以使用 FutureTask 的 get 方法等待任务执行完成返回结果
```java
threadPool.execute(new Runnable(){
    public void run(){
        // task
    }
})

FutureTask<String> future = threadPool.submit(new Callable<String>(){
    public String call(){
        // task
    }
})
String result = future.get();
```

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


```shutdownNow``` 方法首先将线程池的状态设置为 STOP 然后尝试停止所有的正在执行或暂停任务的线程并返回等待执行任务的列表

```shutdown``` 方法将线程池的状态设置为 SHUTDOWN 然后中断所有没有执行任务的线程。

线程池关闭后，调用 ```isShutdown``` 方法就会返回 true，但是只有所有的任务都已经关闭后才表示线程池成功关闭，此时调用 ```isTerminate``` 返回 true。

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

## Executor

Executor 框架将任务的提交和任务的执行调度分离开来，使得在设计任务时只需要关注任务的实现逻辑而不用关注任务的执行调度。

Executor 框架由 3 部分组成：任务、执行器 和 执行结果。其中任务主要包括 ```Runnable``` 和 ```Callable``` 接口，执行器包括 ```ExecutorService``` 接口，执行结果包括 ```Future``` 接口。

### 任务

线程池中的任务需要实现 ```Runnable``` 接口或者 ```Callable``` 接口，使用工具类 ```Executors.callable``` 方法可以将 ```Runnable``` 对象转换为 ```Callable``` 对象。使用 ```ExecutorService``` 对象的 ```execute```方法 和 ```submit```方法 可以将 ```Runnable``` 或者 ```Callable``` 任务提交到线程池执行，返回 ```Future``` 对象的结果。
```java
void execute(Runnable command);

Future<?> submit(Runnable task);

<T> Future<T> submit(Callable<T> task);
```

### 执行器

ExecutorService 是 Executor 框架的执行器和调度器，负责任务的执行和调度。Executor 实现了两种执行器，分别是 ```ThreadPoolExecutor``` 和 ```ScheduledThreadPoolExecutor```。其中 ```ThreadPoolExecutor``` 用于执行普通的任务，而 ```ScheduledThreadPoolExecutor``` 用于执行指定延迟的任务。

工具类 ```Executors``` 提供了创建 ```ThreadPoolExecutor``` 和 ```ScheduledThreadPoolExecutor``` 对象的方法，实际使用中需要根据情况自定义线程池的执行器：
- ```newFixedThreadPool```：生成的线程池的 ```corePoolSize``` 和 ```maximumPoolSize``` 都为指定的线程数，阻塞队列是无界的 ```LinkedBlockingQueue```，多余的空闲线程闲置时间为 0 即多余空闲线程立即终止(此处并没有什么作用因为没有多余的线程)；适用于为了满足资源管理的需求，而需要限制当前线程数量的应用场景，适于负载比较重的服务器
- ```newSingleThreadExecutor```：生成的线程池的 ```corePoolSize``` 和 ```maximumPoolSize``` 都是 1，阻塞队列是无界的 ```LinkedBlockingQueue```，多余空闲任务闲置时间为 0；适用于需要保证顺序地执行各个任务，并且在任意时间点不会有多个线程是活动的应用场景
- ```newCachedThreadPool```：生成的线程池的 ```corePoolSize``` 为 0，```maximumPoolSize``` 为 ```Integer.MAX_VALUE```，即有任务提交时就会创建新的线程去执行(有可能由于线程数过多导致 CPU 和内存资源耗尽)，阻塞队列是 ```SynchronousQueue```，即阻塞队列不会存储提交的任务，多余空闲线程的闲置时间为 60s；适用于执行很多的短期异步任务的小程序或者是负载较轻的服务器
- ```newScheduledThreadPool```：生成的线程池的 ```corePoolSize``` 为指定线程数，```maximumPoolSize``` 为 ```Inter.MAX_VALUE```，阻塞队列使用 ```DelayQueue``` 用于延时任务存储，多余线程空闲时间为 0；适用于需要多个后台线程执行周期任务同时为了满足资源管理的需求而需要限制后台线程数量的场景
- ```newSingleThreadScheduledExecutor```：生成的线程池的 ```corePoolSize``` 为 1，只适用于需要单个后台线程执行周期任务同时需要确保顺序的执行各个任务的场景  

### 执行结果

Executor 框架的任务执行结果使用 ```Future``` 接口表示，Java 中 ```FutureTask``` 是 ```Future``` 接口的实现类。

FutureTask 的 ```get``` 方法阻塞的获取到任务执行的结果，在任务还没有执行完之前，该方法一直阻塞。除此之外 FutureTask 还提供了 ```cancel``` 方法取消任务的执行，以及 ```isDone``` 和 ```isCancel``` 方法判断任务的状态。


*[Back](../)*