## FutureTask

`FutureTask` 实现了 `Future` 接口，表示异步任务执行的结果。此外 `FutureTask` 还实现了 `Runnable` 接口，因此也可以直接调用 run 方法执行。

`FutureTask` 内部维护了 volatile 变量 `state` 表示任务执行的状态，在任务执行的不同时刻期状态是相互转换：

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

`FutureTask` 的最初状态是 `NEW`，状态的改变只能通过 `set`、`setException` 和 `cancel` 方法进行，并且状态的转换只能是固定的：

```
// 正在设置执行结果
NEW -> COMPLETING -> NORMAL
NEW -> COMPLETING -> EXCEPTIONAL
NEW -> CANCELLED
// 正在执行 cancel
NEW -> INTERRUPTING -> INTERRUPTED
```





https://segmentfault.com/a/1190000016572591