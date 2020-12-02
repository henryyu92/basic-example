package example.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BioClientHandler implements Runnable {

    private final Socket socket;

    public BioClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        handle();
    }

    private void handle() {

        if (socket.isConnected()) {
            onConnected(socket.getOutputStream());
        }
        while (true) {
            try {

                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                if (input.available() > 0) {
                    onRead(input, output);
                }
            } catch (Exception e) {
                onException(e);
            }
        }

    }

    protected void onException(Throwable cause) {
        cause.printStackTrace();
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRead(InputStream input, OutputStream out) {
    }

    public void onConnected(OutputStream out) throws IOException {
        out.write("Hello world\n".getBytes());
        out.flush();
    }
}
