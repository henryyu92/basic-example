## 线程池

Java 线程和操作系统线程是一对一的关系，创建一个 Java 线程创建时会创建一个本地操作系统线程，当 Java 线程终止时本地线程也会被回收。

在大型系统中会有大量的线程存在，如果使用直接创建线程和销毁线程的方式，则会带来大量的系统开销。Java 提供了线程池体系使得线程可以被复用，而不是在需要的时候创建新线程，线程处理完任务后就直接销毁，从而减少了系统频繁创建线程和销毁线程带来的巨大开销。

使用线程池至少可以有 3 个方面的好处：
- 降低资源消耗：通过重复利用已创建的线程降低线程创建和销毁造成的消耗
- 提高响应效率：当任务队列到达时，任务可以不需要等到线程创建就能立即执行
- 提高线程的可管理性：使用线程池可以进一步分配、调优和监控线程

### 创建线程池

Java 中 ```ThreadPoolExecutor``` 表示线程池，其在创建时需要传入几个核心参数：```corePoolSize```, ```maximumPoolSize```, ```keepAliveTime```, ```unit```, ```workQueue```, ```threadFactory```, ```handler```。
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

- ```corePoolSize``` 表示线程池中保持的线程的数量，当任务提交到线程池时如果当前工作线程数小于 corePoolSize 那么即使其他线程空闲也会创建新线程执行任务。
- ```maximumPoolSize``` 表示线程池允许创建的最大线程数，如果队列满了并且已创建的线程数小于最大线程数就会创建新线程执行任务
- ```keepAliveTime``` 表示超过 corePoolSize 的线程保持存活的最长时间
- ```unit``` 表示线程存活时间的单位
- ```workQueue``` 是一个阻塞队列， 当工作线程数超过 corePoolSize，新提交的任务会加入此阻塞队列
- ```threadFactory``` 是线程池创建线程的工厂，可以通过这个工厂给创建的线程设置有意义的名字
- ```handler``` 是线程池的拒绝策略，当阻塞队列和线程池都满了，对新提交的任务采用拒绝策略，Java 提供了 4 中拒绝策略：```AbortPolicy```(直接抛出异常)，```CallerRunsPolicy```(执行 execute 方法所在线程来运行任务)，```DiscardOldestPolicy```(丢弃队列头的任务)，```DiscardPolicy```(直接丢弃当前任务)。默认采用 AbortPolicy

### 任务提交

JDK 提供两种方式向线程池提交任务：
- execute 方法用于提交不需要返回值的任务
- submit 方法用于提交需要返回值的任务，返回 FutureTask 的异步结果使用 get 方法等待任务执行完成返回结果
```java
threadPool.execute(new Runnable(){
    public void run(){

    }
})

FutureTask<String> future = threadPool.submit(new Callable<String>(){
    public String call(){

    }
})
String result = future.get();
```
### 任务处理
当提交一个新任务到线程池时，线程池的处理流程：
1. 线程池判断核心线程池里面的线程是否都在执行任务，如果不是则创建新的工作线程执行任务，如果核心线程池里的工作线程都在执行任务则进入下个流程
2. 线程池判断工作队列是否已满，如果没有满则将新提交的任务存储在这个工作队列中，如果工作队列已满则进入下个流程
3. 线程池判断线程池的线程是否都处于工作状态，如果没有则创建新的工作线程执行任务，如果都处于工作状态则将这个任务交给饱和策略处理

ThreadPoolExecutor 作为线程池的实现执行 execute 的流程：
1. 如果当前运行的线程少于 corePoolSize 则创建新线程来执行任务
2. 如果运行的线程等于或多于 corePoolSize 则将任务加入 BlockingQueue
3. 如果无法将任务加入 BlockingQueue 则创建新的线程来处理任务
4. 如果创建新线程将使当前运行的线程超出 maximumPoolSize 则会拒绝任务并调用 ```RejectedExecutionHandler.rejectedExecution``` 方法
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

可以通过 ```shutdown()``` 或者 ```shutdownNow()``` 方法来关闭线程池，原理是遍历线程池中的工作线程然后调用线程的 ```interrupt()``` 方法来中断线程。二者的区别在于 ```shutdownNow()``` 首先将线程池的状态设置为 STOP 然后尝试停止所有的正在执行或暂停任务的线程并返回等待执行任务的列表；```shutdown()``` 只是将线程池的状态设置为 SHUTDOWN 然后中断所有没有正在执行任务的线程。

线程池关闭后，调用 ```isShutdown()``` 方法就会返回 true，但是只有所有的任务都已经关闭后才表示线程池成功关闭，此时调用 ```isTerminate()``` 返回 true。

### 线程池配置

根据线程池的特性，可以根据任务的不同配置不同的参数达到更佳的性能：
- CPU 密集型任务应配置尽可能少的线程，如配置 CPU 核数 + 1 个线程的线程池
- IO 密集型任务需要配置尽可能多的线程，如 2*CPU 核数 个线程的线程池
- 如果是 CPU 密集型和 IO 密集型混合任务，则尽量将二者分开
- 优先级不同的任务可以使用优先级队列 PriorityBlockingQueue，这样高优先级的任务会优先执行
- 阻塞队列应该配置为有界队列，避免过多的线程阻塞导致内存溢出

### 线程池监控

线程池提供了可以监控线程池状态的方法：
- ```getTaskCount()```：线程池需要执行的任务数量
- ```getCompletedTaskCount()```：线程池运行过程中已完成的任务数量
- ```getLargestPoolSize()```：线程池曾经创建过的最大线程数量
- ```getPoolSize()```：线程池的线程数
- ```getActiveCount()```：获取活动线程数

通过继承线程池来自定义线程池，重写线程池的 ```beforeExecute()```、```afterExecute()``` 和 ```terminated()``` 方法可以在任务执行前、执行后和线程池关闭前执行一些代码进行监控。
```java
```
## Executor
在 HotSpot 虚拟机的线程模型中，Java 线程一对一的映射为本地操作系统线程，Java 线程创建时会创建一个本地操作系统线程，当 Java 线程终止时本地线程也会被回收。

Java 多线程程序通常将应用分解为若干个任务然后使用用户级的调度器将这些人物映射为固定数量的线程，然后操作系统将这些线程映射到硬件处理器上。
### Executor 框架结构
Executor 框架主要由 3 大部分组成：
- 任务：任务需要实现 Runnable 或者 Callable 接口
- 任务执行：任务执行主要是 ExecutorService 接口的两个实现类 ThreadPoolExecutor 和 ScheduledThreadPoolExecutor
- 任务结果：主要是 Future 接口的实现类 FutureTask

Executor 框架成员详细：
- ThreadPoolExecutor：线程池的核心实现类，用来执行被提交的任务。使用 Executors 工厂类可以创建 TreadPoolExecutor
  - ```newFixedThreadPool```：corePoolSize 和 maximumPoolSize 都为指定的线程数，阻塞队列是无界的 LinkedBlockingQueue，多余的空闲任务闲置时间为 0 即多余空闲线程立即终止(此处并没有什么作用因为没有多余的线程)；适用于为了满足资源管理的需求，而需要限制当前线程数量的应用场景，适于负载比较重的服务器
  - ```newSingleThreadExecutor```：corePoolSize 和 maximumPoolSize 都是 1，阻塞队列是无界的 LinkedBlockingQueue，多余空闲任务闲置时间为 0；适用于需要保证顺序地执行各个任务，并且在任意时间点不会有多个线程是活动的应用场景
  - ```newCachedThreadPool```：corePoolSize 为 0，maximumPoolSize 为 Integer.MAX_VALUE，即有任务提交时就会创建新的线程去执行(有可能由于线程数过多导致 CPU 和内存资源耗尽)，阻塞队列是 SynchronousQueue，即阻塞队列不会存储提交的任务，多余空闲线程的闲置时间为 60s；适用于执行很多的短期异步任务的小程序或者是负载较轻的服务器
- ScheduledThreadPoolExecutor：ThreadPoolExecutor 的子类，主要用于在指定时间之后执行任务，通常用 Executors 工厂类来创建
  - ```newScheduledThreadPool```：corePoolSize 为指定线程数，maximumPoolSize 为 Inter.MAX_VALUE，阻塞队列使用 DelayQueue 用于延时任务存储，多余线程空闲时间为 0；适用于需要多个后台线程执行周期任务同时为了满足资源管理的需求而需要限制后台线程数量的场景
  - ```newSingleThreadScheduledExecutor```：只包含一个线程的 ScheduledThreaPoolExecutor，适用于需要单个后台线程执行周期任务同时需要确保顺序的执行各个任务的场景
- FutureTask：实现 Future 接口，表示异步计算的结果
- Runnable/Callable：实现 Runnable 接口的任务没有返回结果，而实现 Callable 接口的任务可以有返回结果

ScheduledThreadPoolExecutor 

FutureTask

*[Back](../)*