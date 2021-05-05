package example.async;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureMain {

    public void creator(){

        CompletableFuture<Void> fr = CompletableFuture.runAsync(()->{
            System.out.println("runnable");
        });

        CompletableFuture<String> fs = CompletableFuture.supplyAsync(() ->"supply");
    }



}