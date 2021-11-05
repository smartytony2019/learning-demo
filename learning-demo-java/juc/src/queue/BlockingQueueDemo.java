package queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author 熊二
 * @date 11/3/21 4:32 下午
 * @desc file desc
 */
public class BlockingQueueDemo {


    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);

       /* System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));
        System.out.println(blockingQueue.element());


        System.out.println(blockingQueue.add("w"));
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());*/


//        System.out.println(blockingQueue.offer("a"));
//        System.out.println(blockingQueue.offer("b"));
//        System.out.println(blockingQueue.offer("c"));
//        System.out.println(blockingQueue.offer("d"));
//
//        System.out.println(blockingQueue.poll());
//        System.out.println(blockingQueue.poll());
//        System.out.println(blockingQueue.poll());
//        System.out.println(blockingQueue.poll());



//        blockingQueue.put("a");
//        blockingQueue.put("b");
//        blockingQueue.put("c");
////        blockingQueue.put("w");
//
//        System.out.println(blockingQueue.take());
//        System.out.println(blockingQueue.take());
//        System.out.println(blockingQueue.take());
//        System.out.println(blockingQueue.take());





        System.out.println(blockingQueue.offer("a", 3L, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("b", 3L, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("c", 3L, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("d", 3L, TimeUnit.SECONDS));

        System.out.println(blockingQueue.poll(3L, TimeUnit.SECONDS));
        System.out.println(blockingQueue.poll(3L, TimeUnit.SECONDS));
        System.out.println(blockingQueue.poll(3L, TimeUnit.SECONDS));
        System.out.println(blockingQueue.poll(3L, TimeUnit.SECONDS));

    }

}
