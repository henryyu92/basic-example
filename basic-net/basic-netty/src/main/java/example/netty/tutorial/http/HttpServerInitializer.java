package example.netty.tutorial.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslCtx;

    public HttpServerInitializer(SslContext sslCtx){
        this.sslCtx = sslCtx;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline p = ch.pipeline();
        p.addLast(new LoggingHandler(LogLevel.INFO));
        if (sslCtx != null){
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast("httpServerCodec", new HttpServerCodec());
        p.addLast("serverHandler", new HttpServerHandler());
    }
}
