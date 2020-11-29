package example.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BIO Socket Server
 */
public class BIOTimeServer {

    private final int port;

    private final ExecutorService executorPool;

    public BIOTimeServer(int port) {
        this(port, new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                10,
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(1024),
                new ThreadFactory() {
                    AtomicInteger i = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, "BIOTimer-Server-Thread-" + i.getAndIncrement());
                    }
                }));
    }

    public BIOTimeServer(int port, ExecutorService pool) {
        this.port = port;
        this.executorPool = pool;
    }

    public void start() throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        new Thread("Acceptor-Thread"){
            @Override
            public void run() {
                try {
                    Socket socket = serverSocket.accept();
                    executorPool.execute(new TimeHandler(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void main(String[] args) throws IOException {
        BIOTimeServer server = new BIOTimeServer(9900);
        server.start();
    }
}
