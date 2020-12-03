## 多线程
线程是操作系统调度的最小单元，一个进程里可以创建多个线程，每个线程都有各自的计数器、堆栈和局部变量等属性，并且能够访问共享内存变量；一个线程在运行时只能使用一个处理器核心，使用多线程能够更加充分的利用处理器资源。

Java 线程和操作系统线程是一一对应的关系

### 线程状态
Java 定义了 6 种线程状态，在任意一个时间点一个线程有且只能有其中一种状态：
- **New**：创建后尚未调用 start 方法
- **Runnable**：此状态下线程有可能正在执行，也有可能正在等待 CPU 为它分配执行时间
- **Waiting**：处于这种状态的线程不会被分配 CPU 执行时间，它们要等待被其他线程显式的唤醒
- **Timed Waiting**：处于这种状态的线程也不会分配 CPU 执行时间，不过不需要等待被其他线程显式的唤醒或者在等待时间到达后会自动由系统唤醒
- **Blocked**：阻塞状态等待着获取一个排他锁，这个事件将在另一个线程放弃这个锁的时候发生。程序进入同步区域的时候线程将进入这种状态
- **Terminated**：已经终止线程的线程状态，线程已经结束执行

*阻塞状态是线程阻塞在进入 synchonized 代码块时的状态，但是阻塞在 java.util.concurrent 包中 Lock 接口中的线程状态是等待状态，因为对于 Lock 接口的实现是使用了 LockSupport 中相关方法。*

线程在自身的生命周期中并不是固定地处于某个状态，而是随着程序的运行在不同的状态之间切换：

|状态转换 | 转换方法|
|:-|:-|
|New -> Runnable | start()|
|Runnable -> Waiting | wait()/join()/LockSupport.park()|
|Waiting -> Runnable | notify()/notifyAll()/LockSupport.unpark(Thread)|
|Runnable -> Timed Waiting | sleep(long)/wait(long)/join(long)/LockSupport.parkNanos(long)/LockSupport.parkUntil()|
|Timed Waiting -> Runnable | notify()/notifyAll()/LockSupport.unpark(Thread)/slepp() 时间到达|
|Runnable -> Blocked | 等待进入 synchronized|
|Blocked -> Runnable | synchronized 获取到锁|
|Runnable -> Terminated | run() 运行结束|
### 线程创建
线程对象在构造时需要提供线程所需要的属性，如线程所属的线程组、线程优先级、是否 Daemon 线程等。
```java
private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {

  // 当前线程作为新创建线程的 parent 线程
  Thread parent = currentThread();
  
  // 从父线程继承线程组
  if (g == null) {
    g = parent.getThreadGroup();
  }
  this.group = g;
  
  // 从 parent 线程继承是否 Daemon 线程，优先级
  this.daemon = parent.isDaemon();
  this.priority = parent.getPriority();

  // 从 parent 线程继承 contextClassLoader
  this.contextClassLoader = parent.contextClassLoader;

  // 设置 target
  this.target = target;
  
  // 从 parent 线程继承可继承的 ThreaLocal
  this.inheritableThreadLocals = ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);

  // 分配 ID
  tid = nextThreadID();
```

一个新构造的线程对象是由其 parent 线程来进行空间分配的，而 child 线程继承了 parent 是否为 Daemon、优先级和加载资源的 contextClassLoader 以及可继承的 ThreadLocal，同时还会分配一个唯一的 ID 来标识 child 线程。
### 启动线程
线程对象在初始化完成之后，调用 start 方法就可以启动这个线程，调用 start 方法后当前线程告知 Java 虚拟机规划新建线程的运行。
### 线程中断
中断可以理解为线程的一个标识位属性，它表示一个运行中的线程是否被其他线程进行了中断操作。其他线程通过调用该线程的 interrupt 方法对其进行中断操作。

线程通过 isInterrupted 方法来判断是否被中断(只有 alive 的线程被中断了才返回 true，否则返回false)，通过静态方法 ```Thread.interrupted()``` 对当前中断标识位复位。

在抛出 InterruptedException 时，Java 虚拟机会先将该线程的中断标识位清除，然后再抛出 InterruptedException，此时调用 isInterrupted 方法返回 false。
```java
public void breakThread(Thread t){
    // false
    System.out.println(t.isInterrupted());
    t.interrupt();
    // true
    System.out.println(t.isInterrupted());
}
```

利用中断操作可以用于控制线程的取消和终止，这样在线程终止时可以清理资源：
```java
public class ThreadBreak {
    public static void main(String[] args) throws InterruptedException {
        Runner one = new Runner();
        Thread t = new Thread(one, "CountThread");
        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
        Runner two = new Runner();
        t = new Thread(two, "CountThread");
        t.start();
        TimeUnit.SECONDS.sleep(1);
        two.cancel();
    }

    private static class Runner implements Runnable{

        private long i;
        private volatile boolean on = true;

        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()){
                i++;
            }
            System.out.println("count = " + i);
        }
        
        public void cancel(){
            on = false;
        }
    }
}
```
### Daemon 线程
Daemon 线程主要用作程序中后台调度以及支持性工作，调用```Thread.setDaemon(true)```可以将线程设置为 Daemon 线程，设置 Daemon 线程需要在线程启动之前设置。
```java
public class DaemonThread {

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("Daemon Finally !");
            }
        }, "DaemonThread");
        // 需要在启动 Daemon 线程前设置
        t.setDaemon(true);
        t.start();
    }
}
```
虚拟机中只存在 Daemon 线程时将会退出，虚拟机退出时 Daemon 线程中的 finally 代码块不一定会执行，因此不能在 Daemon 线程的 finally 语句块中做资源清理等逻辑。
### ThreadLocal
ThreadLocal 变量是一个以 ThreadLocal 对象为 key、任意对象为 value 的存储结构，这个结构被附带在线程上，也就是说一个线程可以根据一个 ThreadLocal 对象查询到绑定在这个线程上的一个值。
```java
public class Profiler {

    private static final ThreadLocal<Long> TIME_THREADLOCAL = ThreadLocal.withInitial(
        () -> System.currentTimeMillis());

    public static final void begin(){
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }
    public static final long end(){
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }
    public static void main(String[] args) throws InterruptedException {
        Profiler.begin();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Cost: " + Profiler.end());
    }
}
```
### LockSupport
LockSupport 工具类定义了一组公共静态方法，这些方法提供了基本的线程阻塞和唤醒功能，成为构建同步组件的基础工具。
- ```LockSupport.park(blocker)```： 表示阻塞当前线程直到 unpark 或者线程被中断才返回，blocker 表示当前线程等待的对象，主要使用方式是 ```LockSupport.lock(this)```
- ```LockSupport.park(blocker, nanos)```： 带超时的阻塞直到 unpark 或者线程被中断才或者如果超时时间到达，当前线程才退出等待状态
- ```LockSupport.parkUntil(blocker, deadline)```： 阻塞到 deadline 时刻到达或者线程被中断，当前线程退出等待状态
- ```LockSupport.unpark(example.thread)```： 唤醒等待的线程
```java
```
### 等待/通知机制
等待/通知机制是指一个线程 A 调用了对象 O 的 wait() 方法进入等待状态而另一个线程 B 调用了对象 O 的 notify() 方法或者 notifyAll() 方法，线程 A 收到通知后从对象 O 的 wait() 方法返回进而执行后续的操作。

- 使用 wait、notify、notifyAll 方法时需要先对调用对象加锁
- 调用 wait 方法后，线程状态由 Runnable 变更为 Waiting，释放当前线程持有的锁并将当前线程放置到锁对象的等待队列
- notify 和 notifyAll 方法调用后，等待线程需要等到 notify 或 notifyAll 释放锁后才有机会从 wait 方法返回
- notify 方法将等待队列中的一个线程移动到同步队列，notifyAll 方法将等待队列中的所有线程移动到同步队列，被移动的线程状态由 Waiting 转变为 Blocked
- 线程从 wait 方法返回的前提是获取到了调用对象的锁
- notify 方法并不会释放锁，只有在 synchronized 执行完毕才会释放锁

等待/通知的经典范式分为两部分：等待方(消费者)和通知方(生产者)
```java
// 消费者获取对象锁
synchronized(lock){
  // 条件不满足时等待
  while(condition){
    lock.wait();
  }
  // 条件满足后执行
  doSomething();
}

// 生产者获取对象锁
synchronized(lock){
  // 改变条件使条件满足
  change_condition();
  // 通知等待在此对象上的线程
  lock.notifyAll();
}
```
使用等待/通知模型让两个线程交替打印奇偶数：
```java
Object lock = new Object();
int i = 0;

public void printEven(final int max) throws InterruptedException {
    // 获取锁
    synchronized (lock){
        while (i < max){
            // 不满足条件等待
            while (i % 2 != 0){
                lock.wait();
            }
            System.out.println(Thread.currentThread().getName() + ":" + i);
            // 改变条件
            i++;
            // 通知等待线程
            lock.notify();
        }
    }
}

public void printOdd(final int max) throws InterruptedException {
    // 获取锁
    synchronized (lock){
        while (i < max){
            // 不满足条件等待
            while (i % 2 == 0){
                lock.wait();
            }
            System.out.println(Thread.currentThread().getName() + ":" + i);
            // 改变条件
            i++;
            // 通知等待线程
            lock.notify();
        }
    }
}
```
### 等待/超时机制
等待/超时机制是在等待/通知机制上添加一个超时时间：
```java
// 消费者
synchronized(lock){
    long future = System.currentTimeMillis() + timeout;
    long remaining = timeout;
    // 在等待时间内等待条件满足
    while(condition && remaining > 0){
        wait(remaining);
        remaining = future - System.currentTimeMillis();
    }
    doSomething();
}
```
使用等待/超时机制从数据库连接池获取连接：
```java
public class ConnectionPool{
    private LinkedList<Connection> pool = new LinkedList();
	
    public ConnectionPool(int initialSize){
        if(initialSize > 0){
            for(int i = 0; i < initialSize; i++){
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public void releaseConnection(Connection connection){
        if(connection != null){
            // 获取锁
            synchronized(pool){
                // 改变条件
                pool.addLast(connection);
                // 通知等待线程
                pool.notifyAll();
            }
        }
    }

    public Connection fetchConnection(long millis) throws InterruptedException{
        synchronized(pool){
            // 无限等待
            if(millis <= 0){
                while(pool.isEmpty()){
                    pool.wait();
                }
                return pool.removeFirst();
            }else{
                long future = System.currentTimeMillis() + millis;
                long remaining = millis;
                // 线程池为空则需要等待
                while(pool.isEmpty() && remaining > 0){
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if(!pool.isEmpty()){
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}
```

**[Back](../..)**