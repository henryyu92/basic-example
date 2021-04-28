## CompletableFuture

`Future` 表示异步任务的执行结果，但是 `Future` 并没有提供通知机制，因此需要阻塞线程等待结果返回。

`CompletableFuture` 扩展了 `Future`，使得异步任务完成之后能够继续执行操作而无需等待。`CompletableFuture` 能够讲任务结果回调和任务在不同的线程中执行，也能将回调作为继续执行的同步函数在与任务相同的线程中执行。

`CompletableFuture` 定义了大量的静态工厂方法使得异步任务的执行可以十分方便：

- `supplyAsync/runAsync`：提交有/无返回值的异步任务

  ```java
  // 异步执行无返回值的任务
  CompletableFuture<Void> f = CompletableFuture.runAsync(()->{});
  // 异步执行有返回值的任务
  CompletableFuture<String> fs = CompletableFuture.supplyAsync(()->"hello");
  ```

  

- `thenApply/thenApplyAsync`：表示某个任务执行完之后执行的动作，任务执行完的返回值作为参数传入回调函数中

  ```java
  
  ```

  

- `thenRun/thenAccpt/thenApply`：接收上一个任务的返回值，但是执行的回调函数没有返回值

  ```java
  CompletableFuture<Void> fr = CompletableFuture
      .supplyAsync(()->"hello")
      // 接收上一个任务的返回值，并作用于一个 Runnable
      .thenRun(()->{});
  
  CompletableFuture<Void> fa = CompletableFuture
      .supplyAsync(()->"hello")
      // 接收上一个任务的返回值，并作用于一个 Consumer
      .thenAccept((str)->{System.out.println(str)});
  
  CompletableFuture<Void> fp = CompletableFuture
      .supplyAsync(()->"hello")
      // 接收上一个任务的返回值，并作用于一个 Function
      .thenApply((str)->str);
  ```

  

- `exceptionally`：指定某个任务执行异常时的回调函数，抛出的异常会作为回调函数的参数传入

- `whenComplete`：指定某个任务执行完之后执行的回调函数，接收任务执行后的返回值或者抛出的异常

- `thenCombile/thenAcceptBoth/runAfterBoth`：将两个 CompletableFuture 组合起来，只有两个任务都执行完了才会执行下个任务，如果任务执行过程中抛出异常则将异常作为执行结果
  - `thenCombile` 会将两个任务的执行结果作为方法参数传入到回调函数中，并且有返回值
  - `thenAcceptBoth` 会将两个任务的执行结果作为方法参数传入到回调函数中，但是没有返回值
  - `runAfterBoth` 回调函数没有入参，也没有返回值

- `applyToEither/acceptEither/runAfterEither`：将两个 CompletableFuture 组合起来，只要其中一个任务执行完了就会执行下个任务，如果任务执行过程中抛出异常则将异常作为执行结果
  - `applyToEither` 会将执行完成的任务的执行结果作为参数传入回调函数，并且有返回值
  - `acceptEither` 会将执行完成的任务的执行结果作为参数传入回调函数，但是没有返回值
  - `runAfterEither`：回调函数没有入参，也没有返回值

- `allOf`：指定多个任务都执行完成后执行的回调函数，任意一个任务执行过程中发生异常时，返回的结果会抛出异常

- `anyOf`：

- `thenCompose`：指定任务执行完之后的回调函数，任务执行的返回值作为参数传入函数并返回新的 `CompletableFuture`

```java
CompletableFuture future = CompletableFuture.supplyAsync(()->{
    
})
.thenApply(r -> {
    
})
.execptionally(e -> {
    
})
```

### 任务提交

`CompletableFuture` 底层采用 `ForkJoinThreadPool` 实现，