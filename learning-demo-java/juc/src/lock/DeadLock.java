package lock;

import java.util.concurrent.TimeUnit;

/**
 * @author 熊二
 * @date 11/2/21 3:11 下午
 * @desc file desc
 */
public class DeadLock {

    static Object a = new Object();
    static Object b = new Object();

    public static void main(String[] args) {

        new Thread(() -> {
            synchronized (a) {
                System.out.println(Thread.currentThread().getName() + ": A锁" + "试图获取B锁");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b) {
                    System.out.println(Thread.currentThread().getName() + ": B锁");
                }
            }
        }, "A").start();

        new Thread(() -> {
            synchronized (b) {
                System.out.println(Thread.currentThread().getName() + ": B锁" + "试图获取A锁");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (a) {
                    System.out.println(Thread.currentThread().getName() + ": A锁");
                }
            }
        }, "B").start();
    }
}
