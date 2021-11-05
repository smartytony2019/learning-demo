package pool;

import java.util.concurrent.*;

/**
 * @author 熊二
 * @date 11/3/21 5:15 下午
 * @desc file desc
 */
public class ThreadPoolDemo02 {


    public static void main(String[] args) {


        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,
                5,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            for (int i = 0; i < 20; i++) {
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
