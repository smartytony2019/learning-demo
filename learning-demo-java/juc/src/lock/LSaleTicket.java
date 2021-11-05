package lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 熊二
 * @date 11/1/21 4:21 下午
 * @desc file desc
 */
public class LSaleTicket {
    public static void main(String[] args) {
        LTicket ticket = new LTicket();

        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket.sale();
            }
        }, "AA").start();

        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket.sale();
            }
        }, "BB").start();

        new Thread(() -> {
            for (int i = 0; i < 40; i++) {
                ticket.sale();
            }
        }, "CC").start();

    }
}


class LTicket {
    private int number = 30;

    private final ReentrantLock lock = new ReentrantLock();


    public void sale() {
        //上锁
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出:" + (number--) + "剩下:" + number);
            }
        } finally {
            lock.unlock();
        }
    }
}
