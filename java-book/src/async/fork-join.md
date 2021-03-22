## Fork/Join
Fork/Join 是一个用于并发执行任务的框架，其把大任务分割成若干个小任务并且最终汇总每个小任务结果后得到大任务结果。Fork 就是把一个大任务切分为若干个子任务并行执行，Join 就是合并这些子任务的执行结果得到最终结果。

```java
public class CountTask extends RecursiveTask<Integer> {

  private static final int THRESHOLD = 2;
  private int start;
  private int end;

  public CountTask(int start, int end) {
	this.start = start;
	this.end = end;
  }
  @Override
  protected Integer compute() {
	int sum = 0;
	boolean canCompute = (end - start) <= THRESHOLD;
	if (canCompute) {
		for (int i = start; i <= end; i++) {
			sum += i;
		}
	} else {
		int middle = start + (end - start) / 2;
		CountTask leftTask = new CountTask(start, middle);
		CountTask rightTask = new CountTask(middle + 1, end);
		leftTask.fork();
		rightTask.fork();
		int leftResult = leftTask.join();
		int rightResult = rightTask.join();
		sum = leftResult + rightResult;
	}
	return sum;
  }
}
```

ForkJoinTask 在执行的时候可能会抛出异常但是该异常无法捕获，因此 ForkJoinTask 提供了 ```isCompletedAbnormally()``` 方法和 ```getException()``` 方法用于检测获取运行时异常。

Fork/Join 使用两个类完成任务的分割和结果的合并：

- RecursiveAction/RecursiveTask：创建自定义的 ForkJoin 任务，继承 RecursiveAction（无返回结果）或者 RecursiveTask（有返回结果），并且执行 fork 和 join 操作
- ForkJoinPool：执行分割出来的子任务，使用工作窃取算法来执行任务

Fork/Join 机制采用工作窃取 (work-stealing) 算法实现：每个工作线程都拥有自己的任务队列，工作线程从队列尾部获取任务执行，线程完成自己任务队列中的任务后，从其他线程的任务队列头部获取任务执行。

### ForkJoinPool

ForkJoinPool 由 ForkJoinTask 数组和 ForkJoinWorkerThread 数组构成，ForkJoinTask 数组负责存放程序提交给 ForkJoinPool 的任务，ForkJoinWorkerThread 数组负责执行这些任务。

```java

```

### fork
当调用 ForkJoinTask 的 fork 方法时，程序会调用 ForkJoinWorkerThread 的 pushTask 方法异步执行这个任务，然后立即返回结果：
```java
public final ForkJoinTask<V> fork() {
    Thread t;
    if ((t = Thread.currentThread()) instanceof ForkJoinWorkerThread)
        ((ForkJoinWorkerThread)t).workQueue.push(this);
    else
        ForkJoinPool.common.externalPush(this);
    return this;
}
```
### join

Join 方法的主要作用是阻塞当前线程并等待获取结果。join 方法调用 doJoin() 方法的到当前任务的状态，通过判断任务的状态决定返回结果还是抛出异常；任务状态有 4 种：完成(NORMAL)、取消(CANCELLED)、信号(SIGNAL)、异常(EXCEPTIONAL)
```java
public final V join() {
    int s;
    if ((s = doJoin() & DONE_MASK) != NORMAL)
        reportException(s);
    return getRawResult();
}
```
```doJoin()``` 方法主要调用 ```doExec()``` 方法，首先判断任务执行状态，如果完成直接返回，否则执行任务；如果任务执行完成则设置状态为 NORMAL，出现异常则记录异常并将状态设置为 EXCEPTIONAL
```java
final int doExec() {
    int s; boolean completed;
    if ((s = status) >= 0) {
        try {
            completed = exec();
        } catch (Throwable rex) {
            return setExceptionalCompletion(rex);
        }
        if (completed)
            s = setCompletion(NORMAL);
    }
    return s;
}
```

**[Back](../)**