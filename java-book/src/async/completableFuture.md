## CompletableFuture

`Future` 表示异步任务的执行结果，但是 `Future` 并没有提供通知机制，因此需要阻塞线程等待结果返回。

`CompletableFuture` 扩展了 `Future`，使得异步任务完成之后能够继续执行操作而无需等待。

`CompletableFuture` 能够将任务结果回调和任务在不同的线程中执行，也能将回调作为继续执行的同步函数在与任务相同的线程中执行。

### 创建异步任务

`CompletableFuture` 定义了大量的静态工厂方法使得异步任务的执行可以十分方便：

- `runAsync` ：创建没有返回值的异步任务
- `supllyAsync`：创建有返回值的异步任务

```java
// 异步执行无返回值的任务
CompletableFuture<Void> f = CompletableFuture.runAsync(()->{});
// 异步执行有返回值的任务
CompletableFuture<String> fs = CompletableFuture.supplyAsync(()->"hello");
```

### 单任务依赖

`CompletableFuture` 可以指定任务的单向依赖，只有在依赖的任务执行完之后才会执行指定的任务。

- `thenRun/thenRunAsync`：如果上阶段的任务正常执行则执行给定的 `Runnable`
- `thenAccept/thenAcceptAsync`：

- `thenApply/thenApplyAsync`：接收上一个任务的返回值，但是执行的回调函数没有返回值

`xxxAsync` 方法表示任务会异步执行，如果没有指定线程池则会在默认的 `ForkJoinPool` 中执行，否则在指定的线程池中执行。

```java
CompletableFuture<Void> fr = CompletableFuture
    .supplyAsync(()->"hello")
    // 上个任务正常执行完后执行指定的 Runnable
    .thenRun(()->{});

CompletableFuture<Void> fa = CompletableFuture
    .supplyAsync(()->"hello")
    // 上个任务正常执行完后将返回的结果作为参数执行指定的 Consumer
    .thenAccept((str)->{System.out.println(str)});

CompletableFuture<Void> fp = CompletableFuture
    .supplyAsync(()->"hello")
    // 上个任务正常执行完后将返回的结果作为参数执行指定的 Function
    .thenApply((str)->str);
```



如果上阶段的任务在执行时发生异常，则 `thenRun/thenAccept/thenApply` 等方法则不会执行，`CompletableFuture` 通过 `whenComplete/exceptionally/handle` 提供了在上阶段任务执行异常时的解决方法：

- `whenComplete/whenCompleteAsync`：上阶段任务执行成功或者异常后执行指定的 `BiConsumer`，其中参数是上阶段任务的返回值和异常(二者中有一个为 null)
- `exceptionally`：上阶段任务执行异常后执行指定的 `Function`，如果上阶段任务正常执行则不会执行指定的逻辑并返回上阶段任务的返回值
- `handle/handleAsync`：上阶段任务执行成功或者异常后执行指定的 `Function`，其中参数是上阶段任务的返回值和异常(二者中有一个为 null)

```java
CompletableFuture<Void> fc = CompletableFuture
    .supplyAsync(()-> throw new RuntimeException("exception"))
    // 接收返回值以及异常，但是不返回结果
    .whenComplete((r, e) -> {
        if(e != null){
            // handle exception
        }else{
            // do something
        }
    });

CompletableFuture<String> fe = CompletableFuture
    .supplyAsync(() -> throw new RuntimeException("exception"))
    // 接收异常，如果上个任务正常执行则返回上个任务的返回值
    .exceptionally(e -> {
        // handle exception
        return "hello";
    });

CompletableFuture<String> fh = CompletableFuture
    .supplyAsync(() -> throw new RuntimeException("exception"))
    .handle((r, e) -> {
        if(e != null){
            // handle exception
            return "";
        }
        return "hello";
    });
```

- `thenCompose`：上阶段任务成功执行后执行指定的 `Function`，任务执行的返回值作为 `Function` 的参数并返回新的 `CompletableFuture`

```java
CompletableFuture<String> f = CompletableFuture
    .supplyAsync(()->"hello")
    .thenCompose(str -> new CompletableFuture(str));
```



### 多任务依赖

`CompletableFuture` 提供了对多任务依赖的处理，支持四种模式：

- `thenCombile/thenAcceptBoth/runAfterBoth`：将两个 `CompletableFuture` 组合起来，只有两个任务都执行完了才会执行下个任务，如果任务执行过程中抛出异常则将异常作为执行结果
  - `runAfterBoth/runAfterBothAsync` 回调函数没有入参，也没有返回值
  - `thenCombile/thenCombineAsync` 将两个任务的执行结果作为方法参数传入到回调函数中，有返回值
  - `thenAcceptBoth/thenAcceptBothAsync` 将两个任务的执行结果作为方法参数传入到回调函数中，但是没有返回值
- `applyToEither/acceptEither/runAfterEither`：将两个 `CompletableFuture` 组合起来，只要其中一个任务执行完了就会执行下个任务，如果任务执行过程中抛出异常则将异常作为执行结果
  - `applyToEither` 会将执行完成的任务的执行结果作为参数传入回调函数，并且有返回值
  - `acceptEither` 会将执行完成的任务的执行结果作为参数传入回调函数，但是没有返回值
  - `runAfterEither`：回调函数没有入参，也没有返回值
- `allOf/anyOf`：指定多个任务都执行完成后执行的回调函数，任意一个任务执行过程中发生异常时，返回的结果会抛出异常
  - `allOf`
  - `anyOf`

```java
CompletableFuture<String> f1 = new CompletableFuture("hello");
CompletableFuture<String> f2 = new CompletableFuture("world");

CompletableFuture<Void> fr = f1.runAfterBoth(f2, ()->{});
CompletableFuture<String>
```



### 任务提交

`CompletableFuture` 底层采用 `ForkJoinThreadPool` 实现，