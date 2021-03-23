package example.socket.chat;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class ChatClient {

    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;

    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public ChatClient(){
        try{
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            userName = socketChannel.getLocalAddress().toString().substring(1);


            System.out.println(userName + "is OK!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void sendInfo(String msg){
        msg = userName + "说 " + msg;

        try{

            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readInfo(){
        try{

            int count = selector.select();
            if (count > 0){
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
                    if (key.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        socketChannel.read(buffer);
                        String msg = new String(buffer.array());

                        System.out.println(msg.trim());
                    }
                    it.remove();
                }
            }else{
                System.out.println("没有可用 Channel ");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();

        new Thread(()->{
            while (true){
                chatClient.readInfo();
                try{
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String next = scanner.nextLine();
            chatClient.sendInfo(next);
        }

    }
}
