package sync;

/**
 * @author 熊二
 * @date 11/1/21 3:55 下午
 * @desc file desc
 */
public class SaleTicket {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<30; i++) {
                    ticket.sale();
                }
            }
        },"AA").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<30; i++) {
                    ticket.sale();
                }
            }
        },"BB").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<30; i++) {
                    ticket.sale();
                }
            }
        },"CC").start();

    }

}


class Ticket {
    private int number = 30;
    public synchronized void sale() {
        if(number > 0) {
            System.out.println(Thread.currentThread().getName()+"卖出:"+(number--)+"剩下:"+number);
        }
    }
}

