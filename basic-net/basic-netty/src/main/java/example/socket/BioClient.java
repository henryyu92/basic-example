package example.socket;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BioClient {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8848"));
    private static final String HOST = System.getProperty("host", "127.0.0.1");

    private final ExecutorService threadPool;

    public BioClient(){
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
                    return new Thread(runnable, "bio-client-thread-" + i.getAndIncrement());
                }
            });
        addHook();
    }

    private void addHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));
    }

    public void connect() throws Exception {
        Socket socket = new Socket(HOST, PORT);
        threadPool.submit(new BioClientHandler(socket));
    }

    public static void main(String[] args) throws Exception {
        BioClient client = new BioClient();
        client.connect();
    }
}
