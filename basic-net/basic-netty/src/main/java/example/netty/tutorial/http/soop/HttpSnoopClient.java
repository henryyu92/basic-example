package example.netty.tutorial.http.soop;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.internal.SystemPropertyUtil;

import java.net.URI;

public class HttpSnoopClient {

    private static final boolean SSL = SystemPropertyUtil.getBoolean("ssl", false);
    private static final String HOST = SystemPropertyUtil.get("host", "127.0.0.1");
    private static final int PORT = SystemPropertyUtil.getInt("port", SSL ? 8443 : 8848);
    private static final String URL = (SSL ? "https://" : "http://") + HOST + ":" + PORT + "/";

    private static final Bootstrap b = new Bootstrap();
    private NioEventLoopGroup group;
    private SslContext sslCtx;
    private Channel channel;

    public HttpSnoopClient() throws Exception {

        group = new NioEventLoopGroup();
        sslCtx = sslContext();

        b.group(group).channel(NioSocketChannel.class).handler(new HttpSnoopClientInitializer(sslCtx));

        connect();
    }

    public void connect() throws Exception {
        try{
            channel = b.connect(HOST, PORT).sync().channel();

            channel.closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    public Channel getChannel(){
        return channel;
    }

    public void write() throws Exception {
        URI uri = new URI(URL);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath(), Unpooled.EMPTY_BUFFER);
        request.headers().set(HttpHeaderNames.HOST, HOST)
            .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
            .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        request.headers().set(HttpHeaderNames.COOKIE, ClientCookieEncoder.STRICT.encode(new DefaultCookie("cookie-name", "cookie-value")));

        channel.writeAndFlush(request);
    }

    private SslContext sslContext() throws Exception {
        if (SSL){
            return SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        HttpSnoopClient client = new HttpSnoopClient();
        client.write();
    }
}
