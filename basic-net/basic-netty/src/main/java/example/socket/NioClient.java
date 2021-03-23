package example.socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient {

    public static void main(String[] args) {
        try{
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);

            if (socketChannel.connect(address)){
                while (!socketChannel.finishConnect()){
                    System.out.println("连接中。。。。");
                }
            }

            String str = "hello world";
            ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

            socketChannel.write(buffer);

            System.in.read();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
