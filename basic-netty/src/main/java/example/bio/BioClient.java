package example.bio;


import java.net.InetAddress;
import java.util.concurrent.ThreadPoolExecutor;

public class BioClient {

    private String host;
    private Integer port;

    private ThreadPoolExecutor pool;

    public BioClient(String host, Integer port){
        this.host = host;
        this.port = port;
    }

    public BioClient(InetAddress address){
        host = address.getHostName();
    }


    public static void main(String[] args) {

    }
}
