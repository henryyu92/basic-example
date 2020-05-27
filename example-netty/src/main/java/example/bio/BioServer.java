package example.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    private Integer port;
    private volatile boolean started = false;

    public BioServer(Integer port){
        this.port = port;
        start();
    }


    /**
     * 启动服务端，Main 线程作为 Acceptor 线程，线程池作为链路处理线程
     */
    public void start(){
        if (!started){

            started = true;
            // 创建 ServerSocket 监听端口
            try(ServerSocket serverSocket = new ServerSocket(port)){
                while (started){
                    // 线程阻塞
                    System.out.println("连接线程阻塞： " + Thread.currentThread().getName());
                    Socket socket = serverSocket.accept();
                    System.out.println("建立连接。。。。remote address: " + socket.getRemoteSocketAddress());
                    executor.execute(()->{
                        handle(socket);
                    });
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

    public void handle(Socket socket){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String currentTime;

            while (true) {
                System.out.println("线程读取数据阻塞: " + Thread.currentThread().getName());
                String body = in.readLine();
                if (body == null || body.length() <= 0){
                    break;
                }
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

    public static void run(Integer port){
        new BioServer(port).start();
    }

    public static void main(String[] args) {
        BioServer.run(8888);
    }

}
