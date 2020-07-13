### CopyOnWrite 类
### Atomic 类
Java 提供了 java.util.concurrent.atomic 包，这个包中的原子操作类提供了线程安全的更新，Atomic 类基本都是使用 Unsafe 实现的包装类。
#### 原子更新基本类型
- AtomicBoolean：原子更新布尔类型
- AtomicInteger：原子更新整型
- AtomicLong：原子更新长整型
```java
public class AtomicIntegerTest {

    public static AtomicInteger ai = new AtomicInteger(1);
    public static void main(String[] args) {
        System.out.println(ai.getAndIncrement());
        System.out.println(ai.get());
    }
}
```
#### 原子更新数组
- AtomicIntegerArray：原子更新整型数组里的元素
- AtomicLongArray：原子更新长整型数组里的元素
- AtomicReferenceArray：原子更新引用类型数组里的元素
```java
public class AtomicIntegerArrayTest {

    // AtomicIntegerArray 是将数组进行了复制然后再对数组修改，并不会影响原数组
    public static int[] value = new int[]{1, 2};
    public static AtomicIntegerArray ai = new AtomicIntegerArray(value);
    public static void main(String[] args) {
        ai.getAndSet(0, 3);
        System.out.println(ai.get(0));
        System.out.println(value[0]);
    }
}
```
#### 原子更新引用
- AtomicReference：原子更新引用类型
- AtomicReferenceFieldUpdater：原子更新引用类型里的字段
- AtomicMarkableReference：原子更新带有标记位的引用类型
```java
public class AtomicReferenceTest {
    public static AtomicReference<User> ar = new AtomicReference<>();
    public static void main(String[] args) {
        User user = new User("conna", 10);
        ar.set(user);
        User upUser = new User("Shinichi", 17);
        ar.compareAndSet(user, upUser);
        System.out.println(ar.get().getName());
    }

    static class User{
        private String name;
        private int old;
        public User(String name, int old){
            this.name = name;
            this.old = old;
        }
        public String getName(){
            return this.name;
        }
    }
}
```
#### 原子更新属性
- AtomicIntegerFieldUpdater：原子更新整型的字段的更新器
- AtomicLongFieldUpdater：原子更新长整型的更新器
- AtomicStampedReference：原子更新带有版本号的引用类型，可以用于解决使用 CAS 进行原子更新时出现的 ABA 问题

要想原子的更新字段类需要两步：使用静态方法 ```newUpdater()``` 创建一个更新器并设置想要更新的类和属性；更新类的使用 public volatile 修饰的字段。
```java
public class AtomicIntegerFieldUpdaterTest{
    private static AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater.newUpdater(User.class, "old");
    public static void main(String[] args){
        User conan = new User("conan", 10);
        System.out.println(a.getAndIncrement(conan));
        System.out.println(a.get(conan))
    }

    public static class User{
        private String name;
        public volatile int old;
        public User(String name, int old){
            this.name = name;
            this.old = old;
        }
    }
}
```
### CountDownLatch
CountDownLatch 允许一个或多个线程等待其他线程完成操作。CountDownLatch 的构造函数接收一个 int 类型的参数作为计数器，表示允许等待的线程数，当调用 ```countDown()``` 方法时计数器的值就会减 1，CountDownLatch 的 ```await()``` 方法会阻塞当前线程直到计数器的值为 0。**CountDownLatch 的计数器不能重新初始化或者修改**。
```java
public class CountDownLatchTest{
    static CountDownLatch c = new CountDownLatch(2);

    public static void main(String[] args){
        new Thread(new Runnable(){
            public void run(){
                System.out.println("1");
                c.countDown();
                System.out.println("2");
                c.countDown();
            }
        }).start();

        c.await();
        System.ouot.println("3")
    }
}
```
CountDownLatch 底层实际是使用了共享锁来完成的，CountDownLatch 内部定义了继承自 AbstractQueuedSynchronizer 的静态内部类 Sync，CountDownLatch 的 ```countDown()``` 方法和 ```await()``` 方法实际就是释放共享锁和获取共享锁的过程：
```java
private static final class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 4982264981922014374L;

    Sync(int count) {
        setState(count);
    }

    int getCount() {
        return getState();
    }

    protected int tryAcquireShared(int acquires) {
        return (getState() == 0) ? 1 : -1;
    }

    protected boolean tryReleaseShared(int releases) {
        // Decrement count; signal when transition to zero
        for (;;) {
            int c = getState();
            if (c == 0)
                return false;
            int nextc = c-1;
            if (compareAndSetState(c, nextc))
                return nextc == 0;
        }
    }
}
```
### CyclicBarrier
CyclicBarrier 让一组线程到达一个屏障时被阻塞，直到最后一个线程到达屏障时所有被阻塞的线程才会继续运行。CyclicBarrier 的默认构造方法有一个表示屏障拦截的线程数量的参数，每个线程调用 ```await()``` 方法告诉 CyslicBarrier 当先线程已将到达屏障并被阻塞。
```java
public class CyclicBarrierTest{
    static CyclicBarrier c = new CyclicBarrier(2);

    public static void main(String[] args){
        new Thread(new Runnable(){
            public void run(){
                try{
                    c.await();
                }catch(Exception e){

                }
                System.out.println("1");
            }
        }).start();

        try{
            c.await();
        }catch(Exception e){

        }
        System.out.println("2");
    }
}
```
CyclicBarrier 提供一个高级的构造函数 ```CyclicBarrier(int parties, Runnable barrierAction)``` 用于在线程到达屏障时优先执行 barrierAction。
```java
public class CyclicBarrierTest{
    static CyclicBarrier c = new CyclicBarrier(2, new A());

    public static void main(String[] args){
        new Thread(new Runnable(){
            public void run(){
                try{
                    c.await();
                }catch(Exception e){

                }
                System.out.println("1");
            }
        }).start();

        try{
            c.await();
        }catch(Exception e){

        }
        System.out.println("2");
    }

    static class A implements Runnable{
        public void run(){
            System.out.println("3");
        }
    }
}
```
CountDownLatch 的计数器只能使用一次，而 CyclicBarrier 的计数器可以使用 ```reset()``` 方法重置，此外 CyclicBarrier 还提供了一些额外的方法：
- ```getNumberWaiting```： 获取阻塞的线程数
- ```isBroken```：阻塞的线程是否被中断

CyclicBarrier 底层使用 ReentrantLock 和 Condition 机制，每个线程调用 ```await()``` 方法时内部维护的 count 就会减 1，当 count 减少为 0 时执行优先的 barrierAction 的 run 方法然后调用 breakBarrier 方法即调用 Condition 的 notifyAll() 方法使之前阻塞的线程重新可以执行。
```java
private int dowait(boolean timed, long nanos) throws InterruptedException, BrokenBarrierException, TimeoutException {
    final ReentrantLock lock = this.lock;
    // 加锁
    lock.lock();
    try {
        final Generation g = generation;
        if (g.broken)
            throw new BrokenBarrierException();
        // 线程中断则唤醒阻塞线程
        if (Thread.interrupted()) {
            breakBarrier();
            throw new InterruptedException();
        }
        // 计数减 1
        int index = --count;
        // 计数减为 0 表示所有线程到达
        if (index == 0) {  // tripped
            boolean ranAction = false;
			try {
				final Runnable command = barrierCommand;
				if (command != null)
					command.run();
				ranAction = true;
				nextGeneration();
				return 0;
			} finally {
				if (!ranAction)
					breakBarrier();
			}
		}

		// loop until tripped, broken, interrupted, or timed out
		for (;;) {
			try {
				if (!timed)
					trip.await();
				else if (nanos > 0L)
					nanos = trip.awaitNanos(nanos);
			} catch (InterruptedException ie) {
				if (g == generation && ! g.broken) {
					breakBarrier();
					throw ie;
				} else {
					// We're about to finish waiting even if we had not
					// been interrupted, so this interrupt is deemed to
					// "belong" to subsequent execution.
					Thread.currentThread().interrupt();
				}
			}

			if (g.broken)
				throw new BrokenBarrierException();

			if (g != generation)
				return index;

			if (timed && nanos <= 0L) {
				breakBarrier();
				throw new TimeoutException();
			}
		}
	} finally {
		lock.unlock();
	}
}
	
	
private void nextGeneration() {
	// signal completion of last generation
	trip.signalAll();
	// set up next generation
	count = parties;
	generation = new Generation();
}
	
	
private void breakBarrier() {
	generation.broken = true;
	count = parties;
	trip.signalAll();
}
```
### Semaphore
Semaphore 是用来控制同时访问特定资源的线程数量，它通过协调各个线程保证合理的使用公共资源。Semapher 可用于做流量控制，特别是公共资源有限的应用场景比如数据库连接。Semapher 的构造函数接收一个整型参数表示可用的许可证数量，也就是最大并发数量，使用 ```acquire()``` 获取一个许可证，使用完成之后调用 ```release()``` 方法归还许可证。
```java
public class SemaphoreTest{
    private static final int THRED_COUNT = 30;

    private static ExecutorService thrreadPool = Executors.newFixedExecutorPool(THREAD_COUNT);

    private static Semaphore s = new Semaphore(10);

    public static void main(String[] args){
        for(int i = 0; i < THREAD_COUNT; i++){
            threadPool.execute(new Runnable(){
                public void run(){
                    try{
                        s.acquire();
                        System.out.println("save data");
                        s.release();
                    }catch(InterruptedException e){

                    }
                }
            });
        }
        threadPool.shutdown();
    }
}
```
Semapher 还提供了一些其他方法：
- ```availablePermits()```：返回此信号量中当前可用的许可证
- ```getQueueLength()```：返回正在等待获取许可证的线程数
- ```hasQueuedThreads()```：是否有线程正在等待获取许可证
### Exchanger
Exchanger 用于进行线程间的数据交换，它提供一个线程间交换数据的同步点，线程之间通过 ```exchange``` 方法交换数据，如果一个线程先执行 ```exchange``` 方法则会一直等待直到第二个线程执行 ```exchange``` 方法，当两个线程都到达同步点时这两个线程就可以交换数据。
```java
public class ExchangerTest{
    private static final Exchanger<String> exgr = new Exchanger();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args){
        threadPool.execute(new Runnable(){
            public void run(){
                try{
                    String A = "test1";
                    exgr.exchange(A);
					System.out.println(A);
                }catch(InterruptedException e){

                }
            }
        });

        threadPool.execute(new Runnable(){
            public void run(){
                try{
                    String B = "test2";
                    exgr.exchange(B);
					System.out.println(B);
                }catch(InterruptedException e){

                }
            }
        });

        threadPool.shutdown();
    }
}
```

**[Back](../)**