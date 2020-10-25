package example.io.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) {
        try{

            ServerSocketChannel server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(6666));
            server.configureBlocking(false);

            Selector selector = Selector.open();
            // 注册 Channel
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (true){
                if (selector.select(1000) == 0){
                    System.out.println("没有事件发生");
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()){
                    SelectionKey selectionKey = it.next();
                    if (selectionKey.isAcceptable()){
                        SocketChannel socketChannel = server.accept();
                        System.out.println("连接成功。。。。");
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    }else if (selectionKey.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                        socketChannel.read(buffer);
                        System.out.println("from client " + buffer.array());
                    }
                    // 防止重复操作
                    it.remove();
                }

            }

        }catch (Exception e){

        }
    }
}
