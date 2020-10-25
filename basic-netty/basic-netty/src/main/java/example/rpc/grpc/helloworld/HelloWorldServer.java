//package example.rpc.grpc.helloworld;
//
//import io.grpc.BindableService;
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import io.grpc.stub.StreamObserver;
//
//import java.io.IOException;
//
//public class HelloWorldServer {
//
//    private Server server;
//
//    private void start() throws IOException {
//        int port = 50051;
//        server = ServerBuilder
//                .forPort(port)
//                .addService((BindableService)new GreeterImpl())
//                .build()
//                .start();
//
//        Runtime.getRuntime().addShutdownHook(new Thread("shut-down"){
//            @Override
//            public void run() {
//                HelloWorldServer.this.stop();
//            }
//        });
//    }
//
//    private void stop(){
//        if (server != null){
//            server.shutdown();
//        }
//    }
//
//    private void blockUntilShutdown() throws InterruptedException {
//        if (server != null){
//            server.awaitTermination();
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        final HelloWorldServer server = new HelloWorldServer();
//        server.start();
//        server.blockUntilShutdown();
//    }
//
//    static class GreeterImpl extends GreeterGrpc.GreeterImplBase{
//        @Override
//        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
//            HelloReply reply = HelloReply.newBuilder().setMessage("hello " + request.getName()).build();
//            responseObserver.onNext(reply);
//            responseObserver.onCompleted();
//        }
//    }
//
//}
