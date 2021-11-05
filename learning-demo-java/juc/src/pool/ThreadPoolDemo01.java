package pool;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 熊二
 * @date 11/3/21 5:15 下午
 * @desc file desc
 */
public class ThreadPoolDemo01 {


    public static void main(String[] args) {

//        ExecutorService threadPool = Executors.newFixedThreadPool(5);
//        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        ExecutorService threadPool = Executors.newCachedThreadPool();


        try {
            for (int i = 0; i < 20; i++) {
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName());
                });
            }

        } catch (Exception ex) {

        } finally {
            threadPool.shutdown();
        }


    }

}
