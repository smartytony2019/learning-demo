package readwrite;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 熊二
 * @date 11/3/21 2:26 下午
 * @desc 锁降级
 */
public class Demo02 {

    public static void main(String[] args) {

        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        //1. 获取写锁
        writeLock.lock();
        System.out.println("write lock");

        //2. 获取读锁
        readLock.lock();
        System.out.println("read lock");

        //3. 释放写锁
        writeLock.unlock();

        //4. 释放读锁
        readLock.unlock();


    }

}
