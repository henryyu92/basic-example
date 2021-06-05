package example.netty.tutorial.http.soop;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;

public class HttpSnoopClientInitializer extends ChannelInitializer {

    private final SslContext sslCtx;

    public HttpSnoopClientInitializer(SslContext sslCtx){
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        if (sslCtx != null){
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        p.addLast(new HttpClientCodec());
        p.addLast(new HttpContentDecompressor());
        p.addLast(new HttpSnoopClientHandler());
    }
}
