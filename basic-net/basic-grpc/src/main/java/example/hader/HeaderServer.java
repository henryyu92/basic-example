package example.hader;

import example.helloworld.GreeterImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HeaderServer {

    private static final Logger logger = Logger.getLogger(HeaderClient.class.getName());

    private static final int PORT = 50051;
    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(ServerInterceptors.intercept(new GreeterImpl(), new HeaderServerInterceptor()))
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    HeaderServer.this.stop();
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

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final HeaderServer server = new HeaderServer();
        server.start();
        server.blockUntilShutdown();
    }
}
