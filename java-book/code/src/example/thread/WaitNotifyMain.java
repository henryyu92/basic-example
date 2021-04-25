package example.thread;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Wait-Notify 模型：
 * Wait 线程循环判断条件，如果条件满足则执行 Action，否则线程进入 Waiting 状态，此时 Wait 线程会释放锁资源
 * Notify 线程首先执行 Action，然后唤醒 Wait 线程，如果当前锁的等待队列没有 Waiting 状态线程，则没有任何影响
 * <p>
 * Wait-Notify 模型类似于“生产者-消费者”模型，不同之处在于 “生产者-消费者”模型中消费者主动循环 pull 数据，当条件不满足时会一直循环而不会释放资源
 */
public class WaitNotifyMain {

    final Object lock = new Object();
    int count = 0;
    int max;

    public WaitNotifyMain(int max){
        this.max = max;
    }


    private void print(Predicate<Integer> p, Consumer<Integer> c) {
        while (count < max){
            synchronized (lock) {
                // 条件不满足则等待
                while (!p.test(count)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 满则则执行
                c.accept(count);
                // 改变条件
                count++;
                // 通知其他线程
                lock.notify();
            }
        }

    }


    public static void main(String[] args) throws InterruptedException {

        WaitNotifyMain wn = new WaitNotifyMain(100);

        Thread evenThread = new Thread(() -> wn.print(x -> x % 2 == 0, System.out::println), "evenThread");
        evenThread.start();

        Thread oddThread = new Thread(() -> wn.print(x -> x % 2 != 0, System.out::println), "oddThread");
        oddThread.start();

    }

}
