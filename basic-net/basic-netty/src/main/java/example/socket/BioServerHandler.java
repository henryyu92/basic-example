package example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class BioServerHandler {

    private final Socket socket;

    public BioServerHandler(Socket socket) {
        this.socket = socket;
        handle();
    }

    public void handle() {
        try{
            while (true){
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                if (in.available() > 0){
                    onRead(in, out);
                }
            }
        }catch (Exception e){
            onException(e);
        }
    }

    public void onException(Throwable cause){
        cause.printStackTrace();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRead(InputStream input, OutputStream output) throws Exception{

        System.out.println("read thread: " + Thread.currentThread().getName());
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String body = in.readLine();
        if (body == null || body.length() <= 0){
            return;
        }
        System.out.println("The time server receive order: " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        try (PrintWriter out = new PrintWriter(output, true)) {

            out.println(currentTime);
            out.flush();
        }
    }

}
