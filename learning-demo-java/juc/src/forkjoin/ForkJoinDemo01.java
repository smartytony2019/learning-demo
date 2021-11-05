package forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author 熊二
 * @date 11/3/21 6:19 下午
 * @desc file desc
 */
public class ForkJoinDemo01 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyTask myTask = new MyTask(0, 100);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> forkJoinTask = forkJoinPool.submit(myTask);
        Integer result = forkJoinTask.get();
        System.out.println(result);
        forkJoinPool.shutdown();
    }
}

class MyTask extends RecursiveTask<Integer> {
    private static final Integer VALUE = 10;
    private int begin;
    private int end;
    private int result;

    public MyTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if ((end - begin) < VALUE) {
            for (int i = begin; i <= end; i++) {
                result += i;
            }
        } else {
            int middle = (begin + end) / 2;
            MyTask myTask01 = new MyTask(begin, middle);
            MyTask myTask02 = new MyTask(middle + 1, end);
            myTask01.fork();
            myTask02.fork();
            result = myTask01.join() + myTask02.join();
        }
        return result;
    }
}

