package example.socket;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BioClient {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8848"));
    private static final String HOST = System.getProperty("host", "127.0.0.1");

    private List<Socket> sockets;

    public BioClient() throws Exception {
        Socket socket = new Socket(HOST, PORT);
        sockets.add(socket);
    }

    public List<Socket> getSockets(){
        return sockets;
    }



    public void write(Socket socket, Object msg) throws Exception{
        if (socket.isClosed()){
            return;
        }
        OutputStream output = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        oos.writeObject(msg);
        oos.flush();
    }

    public static void main(String[] args) throws Exception {
        BioClient client = new BioClient();
        List<Socket> sockets = client.getSockets();
        for (Socket socket : sockets){
            client.write(socket, "hello world");
        }
    }
}
