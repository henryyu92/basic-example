package example.rpc.grpc.helloworld;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class HelloWorldClient {

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloWorldClient(Channel channel){
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try{
            response = blockingStub.sayHello(request);
            System.out.println(response);
        }catch (StatusRuntimeException ex){
            return;
        }
    }

    public static void main(String[] args) {
        String user = "world";
        String target = "localhost:50051";
        if (args.length > 0){
            user = args[0];
        }
        if (args.length > 1){
            target = args[1];
        }

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        try{
            HelloWorldClient client = new HelloWorldClient(channel);
            client.greet(user);
        }finally {
            channel.shutdownNow();
        }
    }
}
