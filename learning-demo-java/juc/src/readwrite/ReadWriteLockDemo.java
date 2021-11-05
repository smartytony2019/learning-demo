package readwrite;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 熊二
 * @date 11/3/21 1:54 下午
 * @desc file desc
 */
public class ReadWriteLockDemo {


    public static void main(String[] args) {
        MyCache cache = new MyCache();

        for (int i = 1; i <= 5; i++) {
            final int num = i;
            new Thread(() -> {
                cache.put(String.valueOf(num), String.valueOf(num));
            }, String.valueOf(i)).start();
        }

        for (int i = 1; i <= 5; i++) {
            final int num = i;
            new Thread(() -> {
                cache.get(String.valueOf(num));
            }, String.valueOf(i)).start();
        }

    }

}


class MyCache {


    private volatile Map<String, Object> map = new HashMap<>();
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        rwLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "正在写操作" + key);
            TimeUnit.MICROSECONDS.sleep(300);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写完了" + key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            rwLock.writeLock().unlock();
        }
    }


    public Object get(String key) {
        Object result = null;

        rwLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "正在读操作" + key);
            TimeUnit.MICROSECONDS.sleep(300);
            result = map.get(key);
            System.out.println(Thread.currentThread().getName() + "读完了" + key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }

        return result;
    }


}

