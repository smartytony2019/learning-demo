package sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 熊二
 * @date 11/1/21 4:42 下午
 * @desc file desc
 */
public class ThreadNotify2 {
    public static void main(String[] args) {
        Share2 share = new Share2();
        new Thread(() -> {
            for (int i = 1; i < 10; i++) {
                try {
                    share.incr();       //+1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "AA").start();


        new Thread(() -> {
            for (int i = 1; i < 10; i++) {
                try {
                    share.decr();       //-1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "BB").start();



        new Thread(() -> {
            for (int i = 1; i < 10; i++) {
                try {
                    share.incr();       //+1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "CC").start();


        new Thread(() -> {
            for (int i = 1; i < 10; i++) {
                try {
                    share.decr();       //-1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "DD").start();

    }
}


class Share2 {

    private int number = 0;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();


    public void incr() throws InterruptedException {
        lock.lock();
        try {
            while (number != 0) {
                condition.await();
            }

            number++;
            System.out.println(Thread.currentThread().getName() + ":" + number);

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void decr() throws InterruptedException {
        lock.lock();
        try {
            while (number != 1) {
                condition.await();
            }

            number--;
            System.out.println(Thread.currentThread().getName() + ":" + number);

            condition.signalAll();

        } finally {
            lock.unlock();
        }
    }
}

