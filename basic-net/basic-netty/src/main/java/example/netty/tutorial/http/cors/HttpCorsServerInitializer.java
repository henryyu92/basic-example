package example.netty.tutorial.http.cors;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpCorsServerInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslCtx;

    public HttpCorsServerInitializer(SslContext sslContext){
        this.sslCtx = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin()
            .allowNullOrigin().allowCredentials().build();

        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null){
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpObjectAggregator(65535));
        p.addLast(new ChunkedWriteHandler());
        p.addLast(new CorsHandler(corsConfig));
        p.addLast(new OkResponseHandler());
    }
}
