## FutureTask

`FutureTask` 是可以取消的异步计算，实现了 `Future` 和 `Runnable` 接口，因此可以调用 `run` 方法执行异步计算，并且通过 `get` 阻塞的获取计算结果，`FutureTask` 还可以通过调用 `cancel` 方法取消计算任务。

`FutureTask` 异步任务执行完毕之后就可以获取结果，此时计算任务不能再次执行，也不能取消计算。

`FutureTask` 内部维护了 volatile 变量 `state` 表示任务执行的状态，在任务执行时状态会发生转换：

```java
private volatile int state;
private static final int NEW          = 0;
private static final int COMPLETING   = 1;
private static final int NORMAL       = 2;
private static final int EXCEPTIONAL  = 3;
private static final int CANCELLED    = 4;
private static final int INTERRUPTING = 5;
private static final int INTERRUPTED  = 6;
```

`FutureTask` 的最初状态是 `NEW`，终止状态可以是 `NOMARL`、`EXCEPTIONAL` 、`CANCELLED` 和 `INTERRUPTED` 四种，状态只能通过 `set`、`setException` 和 `cancel` 方法从最初状态通过中间状态转换到终止状态：

```
// set 方法
NEW -> COMPLETING -> NORMAL
// setException 方法
NEW -> COMPLETING -> EXCEPTIONAL
NEW -> CANCELLED
// 正在执行 cancel
NEW -> INTERRUPTING -> INTERRUPTED
```





https://segmentfault.com/a/1190000016572591