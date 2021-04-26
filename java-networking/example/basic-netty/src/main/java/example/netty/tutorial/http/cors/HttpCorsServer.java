package example.netty.tutorial.http.cors;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.SystemPropertyUtil;

public class HttpCorsServer {

    private static final boolean SSL = SystemPropertyUtil.getBoolean("ssl", false);
    private static final int PORT = SystemPropertyUtil.getInt("port", SSL ? 8443 : 8848);

    private final ServerBootstrap b = new ServerBootstrap();
    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;

    public HttpCorsServer() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        SslContext sslCtx = sslContext();

        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new HttpCorsServerInitializer(sslCtx));
    }

    public void start() throws Exception {
        try{
            b.bind(PORT).channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private SslContext sslContext() throws Exception {
        if (SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        HttpCorsServer server = new HttpCorsServer();
        server.start();
    }
}
