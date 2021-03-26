package example.retrying;

import example.helloworld.GreeterGrpc;
import example.helloworld.GreeterImpl;
import example.helloworld.HelloReply;
import example.helloworld.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class RetryingServer {

    private static final Logger logger = Logger.getLogger(RetryingServer.class.getName());

    private static final float UNAVAILABLE_PERCENTAGE = 0.5F;

    private static final Random random = new Random();

    private Server server;

    private void start(int port) throws IOException {
        server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build().start();

        DecimalFormat df = new DecimalFormat("#%");
        logger.info("Responding as UNAVAILABLE to " + df.format(UNAVAILABLE_PERCENTAGE) + "requests");
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown-thread"){
            @Override
            public void run() {
                System.err.println("**** shutting down gRPC server since JVM is shutting down");
                try{
                    RetryingServer.this.stop();
                }catch (InterruptedException e){
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null){
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final RetryingServer server = new RetryingServer();
        server.start(50051);
        server.blockUntilShutdown();
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        AtomicInteger retryCounter = new AtomicInteger(0);

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            int count = retryCounter.incrementAndGet();
            if (random.nextFloat() < UNAVAILABLE_PERCENTAGE) {
                logger.info("Returning stubbed UNAVAILABLE error. count: " + count);
                responseObserver.onError(Status.UNAVAILABLE
                    .withDescription("Greeter temporarily unavailable...").asRuntimeException());
            } else {
                logger.info("Returning successful Hello response, count: " + count);
                HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }
        }
    }
}
