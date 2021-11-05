package sync;

/**
 * @author 熊二
 * @date 11/1/21 4:42 下午
 * @desc file desc
 */
public class ThreadNotify {
    public static void main(String[] args) {
        Share share = new Share();
        new Thread(()->{
            for (int i=1; i<10; i++) {
                try {
                    share.incr();       //+1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AA").start();


        new Thread(()->{
            for (int i=1; i<10; i++) {
                try {
                    share.decr();       //-1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BB").start();
    }
}



class Share {

    private int number = 0;


    public synchronized void incr() throws InterruptedException {
        if(number != 0) {
            this.wait();
        }

        number++;
        System.out.println(Thread.currentThread().getName()+":"+number);

        this.notifyAll();
    }

    public synchronized void decr() throws InterruptedException {
        if(number != 1) {
            this.wait();
        }

        number--;
        System.out.println(Thread.currentThread().getName()+":"+number);

        this.notifyAll();

    }


}
