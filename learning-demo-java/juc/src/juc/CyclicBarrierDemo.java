package juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author 熊二
 * @date 11/2/21 5:23 下午
 * @desc file desc
 */
public class CyclicBarrierDemo {

    private static final int NUMBER = 7;

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(NUMBER, () -> {
            System.out.println("成功");
        });

        for (int i = 1; i <= NUMBER; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 收集到");
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }


    }


}
