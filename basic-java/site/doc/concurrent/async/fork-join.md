### Fork/Join
Fork/Join 是一个用于并发执行任务的框架，是一个把大任务分割成若干个小任务最终汇总每个小任务结果后得到大任务结果的框架。Fork 就是把一个大任务切分为若干个子任务并行执行，Join 就是合并这些子任务的执行结果得到最终结果。

工作窃取(work-stealing)算法：某个线程从其他队列里窃取任务来执行。在大任务被切分为子任务之后，为了减少线程间的竞争每个线程都有自己的任务队列，如果有的线程任务处理较快完成自己任务队列的任务之后就会到其他任务队列窃取任务执行，为了避免和其他任务队列对应的线程竞争通常使用双端队列作为任务队列，工作线程从队列头获取任务而窃取线程从队列尾获取任务。工作窃取算法利用线程并行计算、减少了线程之间的竞争，于此同时该算法会消耗更多的系统资源如创建多个线程和双端队列。

Fork/Join 使用两个类完成任务的分割和结果的合并：
- RecursiveAction/RecursiveTask：创建自定义的 ForkJoin 任务，继承 RecursiveAction（无返回结果）或者 RecursiveTask（有返回结果），并且执行 fork 和 join 操作
- ForkJoinPool：执行分割出来的子任务，使用工作窃取算法来执行任务

Fork/Join 框架使用：
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
ForkJoinTask 与一般的 Task 的主要区别在于它需要实现 compute 方法，在这个方法里首先需要判断任务是否足够小，如果足够小就直接执行，如果不足够小就必须分割成两个子任务，每个子任务在调用 fork 方法时又会进入 compute 方法看当前任务是否足够小，如此循环；使用 join 方法会等待子任务执行完并得到结果。

ForkJoinTask 在执行的时候可能会抛出异常但是该异常无法捕获，因此 ForkJoinTask 提供了 ```isCompletedAbnormally()``` 方法和 ```getException()``` 方法用于检测获取运行时异常。

ForkJoinPool 由 ForkJoinTask 数组和 ForkJoinWorkerThread 数组构成，ForkJoinTask 数组负责存放程序提交给 ForkJoinPool 的任务，ForkJoinWorkerThread 数组负责执行这些任务。
#### fork 实现
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
#### join 实现
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