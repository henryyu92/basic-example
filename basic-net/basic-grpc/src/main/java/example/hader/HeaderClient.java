package example.hader;

import example.helloworld.GreeterGrpc;
import example.helloworld.HelloReply;
import example.helloworld.HelloRequest;
import io.grpc.*;
import org.ietf.jgss.ChannelBinding;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeaderClient {

    private static final Logger logger = Logger.getLogger(HeaderClient.class.getName());

    private final ManagedChannel originChannel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HeaderClient(String host, int port){
        originChannel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext().build();
        ClientInterceptor interceptor = new HeaderClientInterceptor();
        // wrap channel
        Channel channel = ClientInterceptors.intercept(originChannel, interceptor);
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    private void shutdown() throws InterruptedException {
        originChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

    public static void main(String[] args) throws Exception {
        // Access a service running on the local machine on port 50051
        HeaderClient client = new HeaderClient("localhost", 50051);
        try {
            String user = "world";
            // Use the arg as the name to greet if provided
            if (args.length > 0) {
                user = args[0];
            }
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}
