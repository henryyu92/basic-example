package bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Java BIO 编程模型
 */
public class BioServer {

    private InetSocketAddress address;
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private volatile boolean started = false;

    public BioServer(InetSocketAddress address){
        this.address = address;
        start();
    }

    public BioServer(String host, Integer port){
        this(new InetSocketAddress(host, port));
    }

    /**
     * 启动服务端
     */
    public void start(){
        if (!started){

            started = true;
            try(ServerSocket serverSocket = new ServerSocket()){
                serverSocket.bind(address);
                while (started){
                    Socket socket = serverSocket.accept();
                    System.out.println("建立连接。。。。");
                    executor.execute(new TimeServerHandler(socket));
                }
            }catch (Exception e){
                e.printStackTrace();
                stop();
            }
        }
    }

    public void stop(){
        if (started){
            started = false;
            executor.shutdown();
        }
    }

    public static void run(String host, Integer port){
        new BioServer(host, port).start();
    }

    public static void main(String[] args) {
        BioServer.run("localhost", 8888);
    }



    class TimeServerHandler implements Runnable{

        private Socket socket;
        public TimeServerHandler(Socket socket){
            this.socket = socket;
        }
        public void run(){

            try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String body;
                String currentTime;
                while ((body = in.readLine()) != null) {
                    System.out.println("The time server receive order: " + body);
                    currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                        out.println(currentTime);
                        out.flush();
                    }
                }
            }catch (SocketException e){
                System.out.println(e.getMessage());
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (!socket.isClosed()){
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
