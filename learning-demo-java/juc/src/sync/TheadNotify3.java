package sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 熊二
 * @date 11/1/21 7:57 下午
 * @desc file desc
 */
public class TheadNotify3 {

    public static void main(String[] args) {
        ShareResource rs = new ShareResource();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                try {
                    rs.print5(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AA").start();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                try {
                    rs.print10(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BB").start();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                try {
                    rs.print15(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"CC").start();

    }

}


class ShareResource {

    private int flag = 1;
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public void print5(int loop) throws InterruptedException {
        lock.lock();
        try {

            //判断
            while(flag != 1) {
                c1.await();
            }


            //干活
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i +": loop->"+loop);
            }

            //通知
            flag = 2;
            c2.signal();
        }finally {
            lock.unlock();
        }
    }

    public void print10(int loop) throws InterruptedException {
        lock.lock();
        try {
            while (flag !=2) {
                c2.await();
            }

            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i +": loop->"+loop);
            }

            flag = 3;
            c3.signal();
        }finally {
            lock.unlock();
        }
    }


    public void print15(int loop) throws InterruptedException {
        lock.lock();
        try {
            while(flag != 3) {
                c3.await();
            }

            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i +": loop->"+loop);
            }

            flag = 1;
            c1.signal();
        }finally {
            lock.unlock();
        }
    }



}

