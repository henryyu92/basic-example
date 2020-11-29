package example.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TimeHandler implements Runnable {

    private final Socket socket;

    public TimeHandler(Socket socket){
        this.socket = socket;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
