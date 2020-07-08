package example.nio.chat;



import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class ChatServer {

    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    public ChatServer(){
        try{
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);

            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void listen(){
        try{

            while (true){
                int count = selector.select(2000);
                if (count > 0){
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()){
                        SelectionKey key = it.next();
                        if (key.isAcceptable()){
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            socketChannel.configureBlocking(false);
                            System.out.println(socketChannel.getRemoteAddress() + "上线");
                        }
                        if (key.isReadable()){
                            readData(key);
                        }

                        it.remove();
                    }
                }else{
                    System.out.println("等待");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }


    private void readData(SelectionKey key) {
        SocketChannel channel = null;
        try{
            channel = (SocketChannel) key.channel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int count = channel.read(buffer);

            if (count > 0){
                String smg = new String(buffer.array());
                System.out.println("from client " + smg);

                sendInfoToOtherClients(smg, channel);
            }

        }catch (Exception e){
            try {
                System.out.println(channel.getRemoteAddress() + "离线了");
                key.cancel();
                channel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void sendInfoToOtherClients(String msg, SocketChannel channel) throws IOException {
        System.out.println("服务器转发消息");

        for (SelectionKey key : selector.keys()){

            Channel ch = key.channel();

            if (ch instanceof SocketChannel && ch != channel){
                SocketChannel dest = (SocketChannel) ch;
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                dest.write(buffer);
            }

        }

    }


    public static void main(String[] args) {
        new ChatServer().listen();
    }
}
