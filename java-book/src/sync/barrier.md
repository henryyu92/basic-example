`CyclicBarrier`

`CyclicBarrier` 能将一组线程阻塞在某个屏障，直到组内所有的线程都到达了屏障时，被阻塞的线程才会继续执行。

`CyclicBarrier` 构造方法需要表示屏障拦截的线程数量的参数，每个线程调用 ```await()``` 方法告诉 `CyslicBarrier` 当先线程已将到达屏障并被阻塞。

```java
public class CyclicBarrierTest{
    static CyclicBarrier c = new CyclicBarrier(2);

    public static void main(String[] args){
        new Thread(() -> {
            try{
                c.await();
            }catch(Exception e){

            }
            System.out.println("1");
        }).start();

        try{
            c.await();
        }catch(Exception e){

        }
        System.out.println("2");
    }
}
```



`CyclicBarrier` 提供一个高级的构造函数 ```CyclicBarrier(int parties, Runnable barrierAction)``` 用于在线程到达屏障时优先执行 `barrierAction`。

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
### 实现原理

`CyclicBarrier` 底层使用 `ReentrantLock` 和 Condition 实现，线程调用 `await` 方法时部维护的 count 就会减 1，当 count 减少为 0 时执行优先的 `barrierAction` 的 run 方法。

阻塞的线程如果中断或者所有线程已经到达则会调用 `breakBarrier` 方法唤醒所有阻塞的线程并且重置构造方法中传入的指定线程数。

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
                    // 调用 condition 的 await 方法释放锁并等待唤醒
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
    // 重置等待线程数
	count = parties;
    // 唤醒所有线程
	trip.signalAll();
}
```