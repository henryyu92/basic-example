package sample.generic.api;

public class WaitNotifyPrintOddEvent {

    private static int count = 0;
    private static final Object lock = new Object();

    Runnable r = ()->{
        synchronized(lock){
            while(count <= 100){
                System.out.println("Thread-" + Thread.currentThread().getName() + ": " + count++);
                lock.notify();
                if (count <= 100){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public void print(){
        new Thread(r, "偶数线程").start();
        new Thread(r, "奇数线程").start();
    }

    public static void main(String[] args){
        WaitNotifyPrintOddEvent oddEvent = new WaitNotifyPrintOddEvent();
        oddEvent.print();
    }
}
