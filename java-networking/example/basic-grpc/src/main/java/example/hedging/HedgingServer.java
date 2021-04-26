package example.hedging;

import example.helloworld.GreeterImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HedgingServer {

    private static final Logger logger = Logger.getLogger(HedgingServer.class.getName());

    private Server server;

    private void start(int port) throws IOException {
        server = ServerBuilder.forPort(port)
            .addService(new GreeterImpl())
            .intercept(new LatencyInjectionInterceptor())
            .build()
            .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    HedgingServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final HedgingServer server = new HedgingServer();
        server.start(50051);
        server.blockUntilShutdown();
    }
}
