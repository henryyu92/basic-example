package example.io.nio;


import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class BufferMain {

    public static void main(String[] args) {

    }

    public static void bufferFlipClear() {

    }

    public static void mappedBuffer() {

        try (RandomAccessFile file = new RandomAccessFile("test.txt", "rw")) {
            FileChannel channel = file.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
            buffer.put(0, (byte) 'H');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scattering：将数据写入到 buffer 时可以采用 buffer 数组依次写入
     * Gathering：从 buffer 读取数据时可以采用 buffer 数组依次读取
     */
    public static void bufferScatteringGathering() {

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(7000);
            serverSocketChannel.socket().bind(address);

            ByteBuffer[] buffers = new ByteBuffer[2];
            buffers[0] = ByteBuffer.allocate(5);
            buffers[1] = ByteBuffer.allocate(3);

            SocketChannel socketChannel = serverSocketChannel.accept();

            int messageLength = 8;
            while (true) {
                int byteRead = 0;
                while (byteRead < messageLength) {
                    long l = socketChannel.read(buffers);
                    byteRead += l;
                    System.out.println("byteRead = " + byteRead);

                    Arrays.asList(buffers)
                            .stream()
                            .map(buffer -> "position = " + buffer.position() + ". limit = " + buffer.limit())
                            .forEach(System.out::println);
                }

                Arrays.asList(buffers).forEach(Buffer::flip);

                long byteWrite = 0;
                while (byteWrite < messageLength){
                    long w = socketChannel.write(buffers);
                    byteWrite += w;
                }

                Arrays.asList(buffers).forEach(Buffer::clear);

                System.out.println("byteRead = " + byteRead + " byteWrite = " + byteWrite);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
