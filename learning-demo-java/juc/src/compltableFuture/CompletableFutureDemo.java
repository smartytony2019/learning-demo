package compltableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author 熊二
 * @date 11/3/21 6:41 下午
 * @desc file desc
 */
public class CompletableFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " --> completableFuture1");
        });
        completableFuture1.get();


        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread().getName() + " --> completableFuture2");
            int i = 1/0;
            return 1000;
        });

        completableFuture2.whenComplete((t,u)->{
            System.out.println("--->t="+t);
            System.out.println("--->u="+u);
        }).get();


    }

}
