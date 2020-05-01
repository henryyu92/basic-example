package example.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelMain {

    public static void main(String[] args) throws Exception {
        fileChannelReadWrite();
    }

    public static void fileChannelReadWrite() throws Exception {

        // 使用 Channel 从文件中读取数据

        // Stream 关闭的时候关联的 Channel 也会关闭
        try(FileOutputStream out = new FileOutputStream("test.txt")){
            FileChannel channel = out.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            String str = "hello world";
            buffer.put(str.getBytes());
            buffer.flip();

            channel.write(buffer);

        }
        // 使用 Channel 从文件中读取
        try(FileInputStream in = new FileInputStream("test.txt")){
            FileChannel channel = in.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            channel.read(buffer);
            buffer.flip();

            System.out.println(new String(buffer.array()));
        }
    }

    /**
     * 使用 Buffer 可以将多个 Channel 串联起来形成一个 Pipeline
     * @param src
     * @param dest
     * @throws Exception
     */
    public static void fileChannelCopy(String src, String dest) throws Exception{
        try(FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dest)){

            FileChannel inChannel = in.getChannel();
            FileChannel outChannel = out.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(512);

            while (true){

                buffer.clear();

                int read = inChannel.read(buffer);
                if (read == -1){
                    break;
                }

                buffer.flip();
                outChannel.write(buffer);
            }

        }
    }

    /**
     * transferFrom/transferTo 方法可以直接从一个 Channel 将数据转移到另外一个 Channel
     * @param src       sourceChannel
     * @param dest      destinationChannel
     * @throws Exception
     */
    public static void fileChannelTransfor(String src, String dest) throws Exception{
        try(FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dest)){

            FileChannel srcChannel = in.getChannel();
            FileChannel destChannel = out.getChannel();

            destChannel.transferFrom(srcChannel, 0, srcChannel.size());

            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        }
    }
}
