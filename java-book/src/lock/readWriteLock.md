## 读写锁

读写锁在同一个时刻可以允许多个读线程访问，但是在写线程访问时所有的读线程和其他写线程均阻塞。读写锁维护了一个写锁和一个读锁，通过分离读锁和写锁使得并发性能相比一般的排他锁有了很大提升。

```ReentrantReadWriteLock``` 是读写锁的实现，它提供了支持公平性、重进入和锁降级的特性，通过 ```ReentrantReadWriteLock``` 可以分析读写锁的读写状态设计、写锁的获取释放、读锁的获取释放以及锁降级。

### 读写状态

读写锁依赖于 AQS 框架实现同步功能，其读写状态就是 AQS 的同步状态。读写锁将整型的同步状态按位分成两部分：高 16 位表示读状态，低 16 位表示写状态。读状态和写状态的变化使用位运算，读状态使用```c >>> 16``` 获取(整型同步状态的高 16 位)，写状态通过```c & ((1 << 16) - 1)```获取(整型同步状态的低 16 位)，这样就实现了读写锁的同步状态的表示。

```java
static final int SHARED_SHIFT   = 16;
static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

/** Returns the number of shared holds represented in count  */
static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
/** Returns the number of exclusive holds represented in count  */
static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
```

### 写锁获取释放

写锁是一个支持重入的排他锁，如果当前线程获取了写锁则增加写状态，如果当前线程在获取写锁时读锁已经被获取或者该线程不是已经获取写锁的线程，则当前线程进入等待状态：
```java
protected final boolean tryAcquire(int acquires){

  Thread current = Thread.currentThread();
  int c = getState();
  // 获取写状态
  int w = exclusiveCount(c);
  // 存在锁
  if (c != 0) {
	  // (Note: if c != 0 and w == 0 then shared count != 0)
	  if (w == 0 || current != getExclusiveOwnerThread())
          // 此时有读锁或者当前线程没有持有写锁
		  return false;
	  if (w + exclusiveCount(acquires) > MAX_COUNT)
		  throw new Error("Maximum lock count exceeded");
	  // Reentrant acquire
	  setState(c + acquires);
	  return true;
  }
  if (writerShouldBlock() ||
	  !compareAndSetState(c, c + acquires))
	  return false;
  setExclusiveOwnerThread(current);
  return true;
}
```
除了重入条件(当前线程为获取了写锁的线程)之外，增加了一个读锁是否存在的判断：如果存在读锁则写锁不能被获取，因为读写锁要确保写锁的操作对读锁可见，如果允许在读锁已被获取的情况下获取写锁那么正在运行其他读线程就无法感知到当前写线程的操作。因此只有等待其他读线程都释放了读锁，写锁才能被当前线程获取，而写锁一旦被获取则其他读写线程的后续访问均被阻塞。

写锁的释放与 `ReentrantLock` 释放的过程类似，每次释放写锁均减少写状态，当写状态为 0 时表示写锁已经释放，从而等待的读写线程能够继续访问读写锁，同时上次写线程的修改对后续的读写线程可见。
### 读锁获取释放
读锁是一个支持重进入的共享锁，它能够被多个线程同时获取，在没有其他写线程访问时，读锁总是能被成功的获取；如果当前线程获取了读锁则增加读状态，如果当前线程在获取读锁的时候写锁已经被其他线程获取则进入等待状态。
```java
protected final int tryAcquireShared(int unused) {
  /*
   * Walkthrough:
   * 1. If write lock held by another example.thread, fail.
   * 2. Otherwise, this example.thread is eligible for
   *    lock wrt state, so ask if it should block
   *    because of queue policy. If not, try
   *    to grant by CASing state and updating count.
   *    Note that step does not check for reentrant
   *    acquires, which is postponed to full version
   *    to avoid having to check hold count in
   *    the more typical non-reentrant case.
   * 3. If step 2 fails either because example.thread
   *    apparently not eligible or CAS fails or count
   *    saturated, chain to version with full retry loop.
   */
  Thread current = Thread.currentThread();
  int c = getState();
  // 其他线程持有写锁则失败
  if (exclusiveCount(c) != 0 &&
	  getExclusiveOwnerThread() != current)
	  return -1;
  // 读锁状态
  int r = sharedCount(c);
  if (!readerShouldBlock() &&
	  r < MAX_COUNT &&
	  compareAndSetState(c, c + SHARED_UNIT)) {
	  if (r == 0) {
		  firstReader = current;
		  firstReaderHoldCount = 1;
	  } else if (firstReader == current) {
		  firstReaderHoldCount++;
	  } else {
		  HoldCounter rh = cachedHoldCounter;
		  if (rh == null || rh.tid != getThreadId(current))
			  cachedHoldCounter = rh = readHolds.get();
		  else if (rh.count == 0)
			  readHolds.set(rh);
		  rh.count++;
	  }
	  return 1;
  }
  return fullTryAcquireShared(current);
}
```
如果其他线程已经获取了写锁则当前线程获取读锁失败进入等待状态；如果当前线程获取了写锁或者写锁未被获取则当前线程增加读状态，成功获取读锁；读锁的每次释放均减少读状态，减少的值是 1<<16
### 锁降级
**锁降级是指写锁降级成读锁，即把持住当前拥有的写锁然后获取到读锁之后才释放写锁的过程**。锁降级中需要先获取读锁然后释放写锁是为了保证数据的可见性，即当先线程获取写锁后修改的数据对其他线程可见，如果不获取读锁直接释放写锁则有可能另外一个线程获取了写锁然后再一次修改同一个数据，则对其他线程来说上次的数据修改不可见，获取读锁后其他写线程将会阻塞直到读锁释放。
```java
public void processData(){
  readLock.lock();
  if(!update){
	  readLock.unlock();
	  writeLock.lock();
	  try{
		  if(!update){
			  update = true;
		  }
		  readLock.lock();
	  }finally{
		  writeLock.unlock();
	  }
  }
}
```

**[Back](../../)**