package juc;

import java.util.concurrent.CountDownLatch;

/**
 * @author 熊二
 * @date 11/2/21 5:11 下午
 * @desc file desc
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 号离开.");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + " - leader离开");
    }

}
