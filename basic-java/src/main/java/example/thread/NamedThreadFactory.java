package example.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命名的线程工厂
 */
public class NamedThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final boolean daemon;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    public NamedThreadFactory(String namePrefix, boolean daemon){
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + "-thread-" + threadNumber.getAndIncrement());
        t.setDaemon(daemon);
        return t;
    }
}
