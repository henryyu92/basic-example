package example.concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConditionMain {

    public static void main(String[] args) {

        Lock lock = new Mutex();
        Condition condition = lock.newCondition();

        try{
            lock.lock();
            condition.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }
}
