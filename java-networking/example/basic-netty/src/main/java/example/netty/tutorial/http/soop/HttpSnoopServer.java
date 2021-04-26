package example.netty.tutorial.http.soop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.SystemPropertyUtil;

public class HttpSnoopServer {

    private static final boolean SSL = SystemPropertyUtil.getBoolean("ssl", false);
    private static final int PORT = SystemPropertyUtil.getInt("port", 8848);

    private final ServerBootstrap b = new ServerBootstrap();
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private SslContext sslCtx;

    public HttpSnoopServer() throws Exception {

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        sslCtx = sslContext();

        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new HttpSnoopServerInitializer(sslCtx));
    }

    public void start() throws Exception {
        try{
            Channel ch = b.bind(PORT).sync().addListener(future -> {
                System.out.println("Open your web browser and navigate to " +
                    (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');
            }).channel();

            // wait for channel close
            ch.closeFuture().sync();
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
        HttpSnoopServer server = new HttpSnoopServer();
        server.start();
    }
}
