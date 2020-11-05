package example.errorhanding;

import com.google.common.base.Verify;
import com.google.common.util.concurrent.ListenableFuture;
import example.helloworld.GreeterGrpc;
import example.helloworld.GreeterImpl;
import example.helloworld.HelloReply;
import example.helloworld.HelloRequest;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ErrorHandingClient {

    private ManagedChannel channel;
    private int port;

    public void run() throws IOException {

        Server server = ServerBuilder.forPort(port).addService(new GreeterGrpc.GreeterImplBase() {
            @Override
            public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                responseObserver.onError(Status.INTERNAL.withDescription("Eggplant Xerxes Crybaby Overbite Narwhal").asRuntimeException());
            }

            @Override
            public void sayHelloAgain(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

                responseObserver.onNext(HelloReply.newBuilder().build());
                responseObserver.onError(Status.INTERNAL.asRuntimeException());
                responseObserver.onCompleted();
            }
        }).build().start();

        channel = ManagedChannelBuilder.forAddress("localhost", server.getPort()).usePlaintext().build();

    }

    private void blockingCall(){
        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
        try{
            stub.sayHello(HelloRequest.newBuilder().setName("Bart").build())
        }catch (Exception e){
            Status status = Status.fromThrowable(e);
            Verify.verify(status.getCode() == Status.Code.INTERNAL);
            Verify.verify(status.getDescription().contains("Eggplant"));
            // Cause is not transmitted over the wire.
        }
    }

    void futureCallDirect() {
        GreeterGrpc.GreeterFutureStub stub = GreeterGrpc.newFutureStub(channel);
        ListenableFuture<HelloReply> response =
                stub.sayHello(HelloRequest.newBuilder().setName("Lisa").build());

        try {
            response.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            Status status = Status.fromThrowable(e.getCause());
            Verify.verify(status.getCode() == Status.Code.INTERNAL);
            Verify.verify(status.getDescription().contains("Xerxes"));
            // Cause is not transmitted over the wire.
        }
    }
}
