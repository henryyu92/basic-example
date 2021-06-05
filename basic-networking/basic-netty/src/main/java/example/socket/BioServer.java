package example.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java BIO
 */
public class BioServer {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8848"));

    private final ExecutorService threadPool;
    private final ServerSocket ss;
    private boolean started;

    public BioServer() throws Exception {
        ss = new ServerSocket(PORT);
        threadPool = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            TimeUnit.MINUTES.toSeconds(30),
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1024),
            new ThreadFactory() {
                final AtomicInteger i = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable runnable) {
                    return new Thread(runnable, "bio-server-thread-" + i.getAndIncrement());
                }
            });
    }

    public void start() throws Exception {
        if (started) {
            return;
        }
        started = true;
        addHook();
        for (; ; ) {
            Socket s = ss.accept();
            threadPool.execute(()-> new BioServerHandler(s));
        }
    }

    private void addHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void close() throws Exception {
        threadPool.shutdown();
        ss.close();
    }

    public static void main(String[] args) throws Exception {
        BioServer server = new BioServer();
        server.start();
    }
}
