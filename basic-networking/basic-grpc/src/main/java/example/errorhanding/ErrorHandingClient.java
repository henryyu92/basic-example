package example.errorhanding;

import com.google.common.base.Verify;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import example.helloworld.GreeterGrpc;
import example.helloworld.HelloReply;
import example.helloworld.HelloRequest;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

public class ErrorHandingClient {

    private ManagedChannel channel;
    private final int port;

    public ErrorHandingClient(int port){
        this.port = port;
    }

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
            stub.sayHello(HelloRequest.newBuilder().setName("Bart").build());
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

    void futureCallback(){
        GreeterGrpc.GreeterFutureStub stub = GreeterGrpc.newFutureStub(channel);
        ListenableFuture<HelloReply> response = stub.sayHello(HelloRequest.newBuilder().setName("Maggie").build());

        final CountDownLatch latch = new CountDownLatch(1);
        Futures.addCallback(response, new FutureCallback<HelloReply>() {
            @Override
            public void onSuccess(@Nullable HelloReply result) {

            }

            @Override
            public void onFailure(Throwable t) {
                Status status = Status.fromThrowable(t);
                Verify.verify(status.getCode() == Status.Code.INTERNAL);
                Verify.verify(status.getDescription().contains("Crybaby"));
                // Cause is not transmitted over the wire..
                latch.countDown();
            }
        }, directExecutor());

        if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }

    void asyncCall(){
        GreeterGrpc.GreeterStub stub = GreeterGrpc.newStub(channel);
        HelloRequest request = HelloRequest.newBuilder().setName("Homer").build();
        final CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<HelloReply> responseObserver = new StreamObserver<HelloReply>(){

            @Override
            public void onNext(HelloReply helloReply) {

            }

            @Override
            public void onError(Throwable throwable) {
                Status status = Status.fromThrowable(throwable);
                Verify.verify(status.getCode() == Status.Code.INTERNAL);
                Verify.verify(status.getDescription().contains("Overbite"));
                // Cause is not transmitted over the wire..
                latch.countDown();
            }

            @Override
            public void onCompleted() {

            }
        };
        stub.sayHello(request, responseObserver);

        if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }

    void advancedAsyncCall() {
        ClientCall<HelloRequest, HelloReply> call =
                channel.newCall(GreeterGrpc.getSayHelloMethod(), CallOptions.DEFAULT);

        final CountDownLatch latch = new CountDownLatch(1);

        call.start(new ClientCall.Listener<HelloReply>() {

            @Override
            public void onClose(Status status, Metadata trailers) {
                Verify.verify(status.getCode() == Status.Code.INTERNAL);
                Verify.verify(status.getDescription().contains("Narwhal"));
                // Cause is not transmitted over the wire.
                latch.countDown();
            }
        }, new Metadata());

        call.sendMessage(HelloRequest.newBuilder().setName("Marge").build());
        call.halfClose();

        if (!Uninterruptibles.awaitUninterruptibly(latch, 1, TimeUnit.SECONDS)) {
            throw new RuntimeException("timeout!");
        }
    }
}
