package example.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                // Http 编解码器
                .addLast("httpServerCodec", new HttpServerCodec())
                .addLast("serverHandler", new HttpServerHandler());
    }
}
