package example.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.internal.SystemPropertyUtil;

import javax.net.ssl.SSLException;

public class EchoClient {

    static final boolean SSL = SystemPropertyUtil.getBoolean("ssl", false);
    static final String HOST = SystemPropertyUtil.get("host", "127.0.0.1");
    static final int PORT = SystemPropertyUtil.getInt("port", 10000);
    static final int SIZE = SystemPropertyUtil.getInt("size", 256);

    private Bootstrap b;

    private void init() throws SSLException {
        final SslContext sslCtx;
        if (SSL){
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }

        EventLoopGroup group = new NioEventLoopGroup();
    }

}
