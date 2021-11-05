package callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author 熊二
 * @date 11/2/21 4:37 下午
 * @desc file desc
 */
public class Demo01 {


    public static void main(String[] args) {


//        new Thread(new MyThread1(),"MyThread1").start();


        FutureTask<Integer> futureTask1 = new FutureTask<>(new MyThread2());

        FutureTask<Integer> futureTask2 = new FutureTask<>(()->{
           return 10220;
        });

        new Thread(futureTask1,"lucy").start();
        new Thread(futureTask2, "mary").start();
        while (!futureTask2.isDone()) {
            System.out.println("wait...");
        }


        try {
            System.out.println("MyThread2--->"+futureTask2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

class MyThread1 implements Runnable {

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }
}

class MyThread2 implements Callable {

    @Override
    public Integer call() throws Exception {
        return 200;
    }
}


