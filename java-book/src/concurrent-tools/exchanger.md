# Exchanger
Exchanger 用于进行线程间的数据交换，它提供一个线程间交换数据的同步点，线程之间通过 ```exchange``` 方法交换数据，如果一个线程先执行 ```exchange``` 方法则会一直等待直到第二个线程执行 ```exchange``` 方法，当两个线程都到达同步点时这两个线程就可以交换数据。
```java
public class ExchangerTest{
    private static final Exchanger<String> exgr = new Exchanger();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args){
        threadPool.execute(() -> {
            try{
                String A = "test1";
                exgr.exchange(A);
                System.out.println(A);
            }catch(InterruptedException e){

            }
        });

        threadPool.execute(() -> {
            try{
                String B = "test2";
                exgr.exchange(B);
                System.out.println(B);
            }catch(InterruptedException e){

            }
        });

        threadPool.shutdown();
    }
}
```